package dispositivo.api.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;

import dispositivo.interfaces.Configuracion;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class FunctionPublisher_APIMQTT {

	protected MqttClient myClient;
	protected MqttConnectOptions connOpt;
	protected String clientId = null;
	protected String mqttBroker = null;

	public void publish_status(String topic, String commando) {
		try { 
			
			MqttMessage message = new MqttMessage(commando.getBytes());
			message.setQos(0);
			myClient.publish(topic, message);
		} catch (MqttException e) { e.printStackTrace(); } 
	} 
	
	public static FunctionPublisher_APIMQTT build( String brokerURL) {
		FunctionPublisher_APIMQTT api = new FunctionPublisher_APIMQTT();
		api.setBroker(brokerURL);
		return api;
	}
	
	protected FunctionPublisher_APIMQTT() {
		
	}
	

	protected void setBroker(String mqttBrokerURL) {
		this.mqttBroker = mqttBrokerURL;
	}
	
	/**
	 * 
	 * runClient
	 * The main functionality of this simple example.
	 * Create a MQTT client, connect to broker, pub/sub, disconnect.
	 * 
	 */
	public void connect(String dispotivoId) {
		// setup MQTT Client
		String clientID = dispotivoId + UUID.randomUUID().toString() + ".subscriber";
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

			// myClient.setCallback(this);
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println( " MQTT publicador Conectado al broker " + this.mqttBroker);
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
	
	public void iniciar(String dispositivoId) {

		if ( this.myClient == null || !this.myClient.isConnected() )
			this.connect(dispositivoId);
		
		// for(IFuncion f : this.dispositivo.getFunciones()) {
		// 	this.subscribe(this.calculateCommandTopic(f));
		// }
		// this.subscribe(this.calculateTopic()); // Formato dispositivo/{DISPOSITIVO-ID}/comandos
	}
	
}
