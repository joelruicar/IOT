package componentes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

// import utils.MySimpleLogger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import interfaces.IFuncion;
import utils.MySimpleLogger;

public class InfoPanel {

	protected String brokerURL = null;
	protected String infoPanelID = null;
	protected String deviceID = null;

	protected Map<String, IFuncion> functions = null;

	protected ArrayList<String> accidentes;
	protected Map<String, Integer> especiales;

	protected RoadPlace rp = null; // simula la ubicación actual
	protected InfoPanel_RoadInfoSubscriber subscriber = null;
	protected MyMqttClient publisher = null;
	protected String baseTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna";

	protected static String clientEndpoint = "a1se7t1dcq6xyp-ats.iot.us-east-1.amazonaws.com"; // replace <prefix> and
																								// <region> with your
																								// own
	protected static String clientId = "IoTDeviceClient-" + UUID.randomUUID().toString(); // replace with your own
																							// client ID. Use unique
																							// client IDs for concurrent
																							// connections.
	protected static String certsDir = "certs/";
	protected static String certID = "fd16159d548a1cdea892f05f82612b34d25387953d7f851d9d74e8a6aafe01f7";
	protected static String certificateFile = certsDir + certID + "-certificate.pem.crt"; // X.509 based certificate
																							// file
	protected static String privateKeyFile = certsDir + certID + "-private.pem.key"; // PKCS#1 or PKCS#8 PEM encoded
																						// private key file
	protected static String loggerId = "my-aws-iot-thing";
	AWSIotQos qos = null;
	AWSIotMqttClient client = null;

	public InfoPanel(String id, String brokerURL, String deviceID) {
		this.infoPanelID = id;
		this.brokerURL = brokerURL;
		this.deviceID = deviceID;

		this.accidentes = new ArrayList<String>();
		this.especiales = new HashMap<>();

		this.publisher = new MyMqttClient(id + ".traffic", this, this.brokerURL);
		publisher.connect();
		this.subscriber = new InfoPanel_RoadInfoSubscriber(id, this, brokerURL);
		subscriber.connect();

		this.client = initClient();

		// CONNECT CLIENT TO AWS IOT MQTT
		// optional parameters can be set before connect()
		this.qos = AWSIotQos.QOS0;
		try {
			client.connect();
			MySimpleLogger.info(loggerId, "Client Connected to AWS IoT MQTT");

		} catch (AWSIotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// PUBLISH a message in a TOPIC
	}

	public static AWSIotMqttClient initClient() {

		// SampleUtil.java and its dependency PrivateKeyReader.java can be copied from
		// the sample source code.
		// Alternatively, you could load key store directly from a file - see the
		// example included in this README.
		KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
		AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

		return client;
	}

	public static void publish(AWSIotMqttClient client, String topic, String payload, AWSIotQos qos) {

		// optional parameters can be set before connect()
		try {
			AWSIotMessage message = new AWSIotMessage(topic, qos, payload);
			client.publish(message);
			MySimpleLogger.info(loggerId, "... PUBLISHED message " + payload + " to TOPIC: " + topic);
		} catch (AWSIotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCurrentRoadPlace(RoadPlace rp) {

		// ############## DESCOMENTAR CUANDO TTMI008 FUNCIONE
		// HttpRequest request = HttpRequest.newBuilder()
		// 		.uri(URI.create(
		// 				"http://ttmi008.iot.upv.es:8182/segment/" + rp.getRoad()))
		// 		.header("Accept", "application/json")
		// 		.header("Content-Type", "application/json")
		// 		.method("GET", HttpRequest.BodyPublishers.noBody())
		// 		.build();
		// HttpResponse<String> response = null;
		// try {
		// 	response = HttpClient.newHttpClient().send(request,
		// 			HttpResponse.BodyHandlers.ofString());
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// } catch (InterruptedException e) {
		// 	e.printStackTrace();
		// }
		// try {
		// 	JSONObject objres = new JSONObject(response.body());
		// 	rp.setStart(objres.getInt("start-kp"));
		// 	rp.setEnd(objres.getInt("end-kp"));
		// } catch (Exception e) {
		// 	e.printStackTrace();
		// }

		// 1.- Si ya teníamos algún suscriptor conectado al tramo de carretera antiguo,
		// primero los desconectamos
		if (this.rp != rp && rp != null && this.rp != null) {
			try {
				subscriber.unsubscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/info");
				subscriber.unsubscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/alerts");
				subscriber.unsubscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/traffic");
				this.rp = rp;
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/info");
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/alerts");
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/traffic");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 2.- Ahora debemos crear suscriptor/es para conocer 'cosas' de dicho tramo de
		// carretra, y conectarlo/s
		// 3.- Debemos suscribir este/os suscriptor/es a los canales adecuados
		else if (this.rp == null && rp != null) {
			this.rp = rp;
			try {
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/info");
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/alerts");
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/traffic");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setAccidente(String accidente) {
		if (!this.accidentes.contains(accidente))
			this.accidentes.add(accidente);

		this.getFuncion("f2").encender();
		AWS_Report("f2", "encender");
	}

	public void removeAccidente(String accidente) {
		if (this.accidentes.contains(accidente))
			this.accidentes.remove(accidente);

		if (this.accidentes.size() == 0) {
			this.getFuncion("f2").apagar();
			AWS_Report("f2", "apagar");
		}
	}

	public void setEspeciales(String especial, int posicion) {
		this.especiales.put(especial, posicion);
		updateEspecialLight();
	}

	public void removeEspeciales(String especial) {
		if (this.especiales.containsKey(especial))
			this.especiales.remove(especial);

		updateEspecialLight();
	}

	public void updateEspecialLight() {
		boolean ascendente = this.rp.getStart() < this.rp.getEnd();
		int m = this.rp.getM();
		boolean parpadear = false;

		if (this.especiales.isEmpty()) {
			this.getFuncion("f3").apagar();
			AWS_Report("f3", "apagar");
			return;
		}

		for (Map.Entry<String, Integer> entry : this.especiales.entrySet()) {
			int diff = m - entry.getValue();
			if (ascendente && diff >= 0) {
				if (diff < 200) {
					this.getFuncion("f3").encender();
					AWS_Report("f3", "encender");
					return;
				} else {
					parpadear = true;
				}
			} else if (!ascendente && diff <= 0) {
				if (diff > -200) {
					this.getFuncion("f3").encender();
					AWS_Report("f3", "encender");
					return;
				} else {
					parpadear = true;
				}
			}
		}
		if (parpadear) {
			this.getFuncion("f3").parpadear();
			AWS_Report("f3", "parpadear");
			return;
		}
		this.getFuncion("f3").apagar();
		AWS_Report("f3", "apagar");
	}

	public void AWS_Report(String target, String action) {
		JSONObject prop = new JSONObject();
		try {
			prop.put(target, action);
		} catch (Exception e) {
			// TODO: handle exception
		}
		String topic = target;
		publish(client, topic, prop.toString(), qos);
	}

	protected Map<String, IFuncion> getFunctions() {
		return this.functions;
	}

	protected void setFunctions(Map<String, IFuncion> fs) {
		this.functions = fs;
	}

	public IFuncion getFuncion(String funcionId) {
		if (this.getFunctions() == null)
			return null;
		return this.getFunctions().get(funcionId);
	}

	public void addFuncion(IFuncion f) {
		if (this.getFunctions() == null)
			this.setFunctions(new HashMap<String, IFuncion>());
		this.getFunctions().put(f.getId(), f);
		return;
	}

}
