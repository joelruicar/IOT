package dispositivo.api.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;

import dispositivo.interfaces.Configuracion;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class Dispositivo_APIMQTT implements MqttCallback {

	protected MqttClient myClient;
	protected MqttConnectOptions connOpt;
	protected String clientId = null;
	
	protected IDispositivo dispositivo;
	protected String mqttBroker = null;
	
	private String loggerId = null;

	FunctionPublisher_APIMQTT publisher_APIMQTT = null;
	
	public static Dispositivo_APIMQTT build(IDispositivo dispositivo, String brokerURL) {
		Dispositivo_APIMQTT api = new Dispositivo_APIMQTT(dispositivo);
		api.setBroker(brokerURL);
		return api;
	}

	public static Dispositivo_APIMQTT build(IDispositivo dispositivo, String brokerURL, FunctionPublisher_APIMQTT publisher_APIMQTT) {
		Dispositivo_APIMQTT api = new Dispositivo_APIMQTT(dispositivo);
		api.setBroker(brokerURL);
		api.setPublisher(publisher_APIMQTT);
		return api;
	}
	
	protected Dispositivo_APIMQTT(IDispositivo dev) {
		this.dispositivo = dev;
		this.loggerId = dev.getId() + "-apiMQTT";
	}
	
	protected void setBroker(String mqttBrokerURL) {
		this.mqttBroker = mqttBrokerURL;
	}
	
	
	@Override
	public void connectionLost(Throwable t) {
		MySimpleLogger.debug(this.loggerId, "Connection lost!");
		// code to reconnect to the broker would go here if desired
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		//System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String payload = new String(message.getPayload());
		
		System.out.println("-------------------------------------------------");
		System.out.println("| Topic:" + topic); // -> dispositivo/{DISPOSITIVO-ID}/comandos
		System.out.println("| Message: " + payload);
		System.out.println("-------------------------------------------------");
		
		String[] topicNiveles = topic.split("/");


		// parse payload to JSONObject
		JSONObject jsonPayload = new JSONObject(payload);
		
		String action = jsonPayload.getString("accion");

		
		
		// Validate if the topic is a command topic for a function
		if(!"funcion".equals(topicNiveles[topicNiveles.length-3])) {

			if ( action.equalsIgnoreCase("habilitar") ){
				MySimpleLogger.info(this.loggerId, "==> Habilitar");
				this.dispositivo.habilitar();
			}else if ( action.equalsIgnoreCase("deshabilitar") ){
				MySimpleLogger.info(this.loggerId, "==> Deshabilitar");
				this.dispositivo.deshabilitar();
			}else
				MySimpleLogger.warn(this.loggerId, "Acción '" + payload + "' no reconocida. Sólo admitidas: habilitar o deshabilitar");

			System.out.println("-------------------------------------------------");
			return;
		}

		//
		// Obtenemos el id de la función
		//   Los topics están organizados de la siguiente manera:
		//         $topic_base/dispositivo/funcion/$ID-FUNCION/commamnd
		//   Donde el $topic_base es parametrizable al arrancar el dispositivo
		//   y la $ID-FUNCION es el identificador de la dunción
		String funcionId = topicNiveles[topicNiveles.length-2];
		
		IFuncion f = this.dispositivo.getFuncion(funcionId);
		if ( f == null ) {
			MySimpleLogger.warn(this.loggerId, "No encontrada funcion " + funcionId);
			return;
		}

		//
		// Definimos una API con mensajes de acciones básicos
		//

		// Ejecutamos acción indicada en campo 'accion' del JSON recibido
		if (!"info".equals(topicNiveles[topicNiveles.length-1])){
			// Si es una acción se manda un mensaje con el estado de la función.
			String topicinfo = Configuracion.TOPIC_BASE + "dispositivo/" + this.dispositivo.getId() + "/funcion/" + f.getId() + "/info";
			String commandoinfo = payload;
			publisher_APIMQTT.publish_status(topicinfo, commandoinfo);
		}
		
		if ( action.equalsIgnoreCase("encender") ){
			f.encender();
		}else if ( action.equalsIgnoreCase("apagar") ){
			f.apagar();
		}else if ( action.equalsIgnoreCase("parpadear") ){
			f.parpadear();
		}else
			MySimpleLogger.warn(this.loggerId, "Acción '" + payload + "' no reconocida. Sólo admitidas: encender, apagar o parpadear");

		System.out.println("-------------------------------------------------");
		
	}

	/**
	 * 
	 * runClient
	 * The main functionality of this simple example.
	 * Create a MQTT client, connect to broker, pub/sub, disconnect.
	 * 
	 */
	public void connect() {
		// setup MQTT Client
		String clientID = this.dispositivo.getId() + UUID.randomUUID().toString() + ".subscriber";
		connOpt = new MqttConnectOptions();
		
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
//			connOpt.setUserName(M2MIO_USERNAME);
//			connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());
		
		// Connect to Broker
		try {
			MqttDefaultFilePersistence persistence = null;
			try {
				persistence = new MqttDefaultFilePersistence("/tmp");
			} catch (Exception e) {
			}
			if ( persistence != null )
				myClient = new MqttClient(this.mqttBroker, clientID, persistence);
			else
				myClient = new MqttClient(this.mqttBroker, clientID);

			myClient.setCallback(this);
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		MySimpleLogger.info(this.loggerId, "Conectado al broker " + this.mqttBroker);

	}
	
	
	public void disconnect() {
		
		// disconnect
		try {
			// wait to ensure subscribed messages are delivered
			Thread.sleep(10000);

			myClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	
	protected void subscribe(String myTopic) {
		
		// subscribe to topic
		try {
			int subQoS = 0;
			myClient.subscribe(myTopic, subQoS);
			MySimpleLogger.info(this.loggerId, "Suscrito al topic " + myTopic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	protected void unsubscribe(String myTopic) {
		
		// unsubscribe to topic
		try {
			myClient.unsubscribe(myTopic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void iniciar() {

		if ( this.myClient == null || !this.myClient.isConnected() )
			this.connect();
		
		if ( this.dispositivo == null )
			return;
		
		for(IFuncion f : this.dispositivo.getFunciones()) {
			this.subscribe(this.calculateCommandTopic(f));
		}
		this.subscribe(this.calculateTopic()); // Formato dispositivo/{DISPOSITIVO-ID}/comandos

	}
	
	
	
	public void detener() {
		
		
		// To-Do
		
	}
	
	protected String calculateCommandTopic(IFuncion f) {
		return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/funcion/" + f.getId() + "/comandos";
	}
	
	protected String calculateInfoTopic(IFuncion f) {
		return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/funcion/" + f.getId() + "/info";
	}

	protected String calculateTopic() {
		return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/comandos";
	}

	public void setPublisher(FunctionPublisher_APIMQTT publisher_APIMQTT) {
		this.publisher_APIMQTT = publisher_APIMQTT;
	}

}
