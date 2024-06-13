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

// import org.eclipse.paho.client.mqttv3.MqttClient;
// import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
// import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.amazonaws.services.iot.client.AWSIotMqttClient;

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
	// protected SmartCar_InicidentNotifier notifier = null;
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

	public InfoPanel(String id, String brokerURL, String deviceID) {

		// this.setSmartCarID(id);
		this.infoPanelID = id;
		this.brokerURL = brokerURL;
		this.deviceID = deviceID;

		this.accidentes = new ArrayList<String>();
		this.especiales = new HashMap<>();

		// this.notifier = new SmartCar_InicidentNotifier(id + ".incident-notifier",
		// this, this.brokerURL);
		// this.notifier.connect();
		this.publisher = new MyMqttClient(id + ".traffic", this, this.brokerURL);
		publisher.connect();
		this.subscriber = new InfoPanel_RoadInfoSubscriber(id, this, brokerURL);
		subscriber.connect();

		AWSIotMqttClient client = initClient();

		// CONNECT CLIENT TO AWS IOT MQTT
		// optional parameters can be set before connect()
		AWSIotQos qos = AWSIotQos.QOS0;
		try {
			client.connect();
			MySimpleLogger.info(loggerId, "Client Connected to AWS IoT MQTT");

		} catch (AWSIotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// PUBLISH a message in a TOPIC

		JSONObject prop = new JSONObject();
		try {
			prop.put("f1", "encendidont");
		} catch (Exception e) {
			// TODO: handle exception
		}
		String topic = "f3";
		publish(client, topic, prop.toString(), qos);

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

	// public void f(String state, String target){
	// System.out.println("State: " + state + "; target: " + target);
	// String topic = "dispositivo/" + this.deviceID + "/funcion/" + target +
	// "/comandos";
	// String message = buildMessage(state);

	// this.publisher.publish(topic, message);

	// }

	// public void setSmartCarID(String smartCarID) {
	// this.smartCarID = smartCarID;
	// }

	// public String getSmartCarID() {
	// return smartCarID;
	// }

	public void setCurrentRoadPlace(RoadPlace rp) {

		// ############## DESCOMENTAR CUANDO TTMI008 FUNCIONE
		// HttpRequest request = HttpRequest.newBuilder()
		// .uri(URI.create(
		// "http://ttmi008.iot.upv.es:8182/segment/" + rp.getRoad()))
		// .header("Accept", "application/json")
		// .header("Content-Type", "application/json")
		// .method("GET", HttpRequest.BodyPublishers.noBody())
		// .build();
		// HttpResponse<String> response = null;
		// try {
		// response = HttpClient.newHttpClient().send(request,
		// HttpResponse.BodyHandlers.ofString());
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// try {
		// JSONObject objres = new JSONObject(response.body());
		// rp.setStart(objres.getInt("start-kp"));
		// rp.setEnd(objres.getInt("end-kp"));
		// } catch (Exception e) {
		// e.printStackTrace();
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
	}

	public void removeAccidente(String accidente) {
		if (this.accidentes.contains(accidente))
			this.accidentes.remove(accidente);

		if (this.accidentes.size() == 0)
			this.getFuncion("f2").apagar();
	}

	public void setEspeciales(String especial, int posicion) {
		this.especiales.put(especial, posicion);
		updateEspecialLight();

		// int rel_pos = this.rp.getM() - 200;
		// if (rel_pos < 200 && rel_pos > -200)
		// this.getFuncion("f3").encender();
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
			return;
		}

		for (Map.Entry<String, Integer> entry : this.especiales.entrySet()) {
			int diff = m - entry.getValue();
			if (ascendente && diff >= 0) {
				if (diff < 200) {
					this.getFuncion("f3").encender();
					return;
				} else {
					parpadear = true;
				}
			} else if (!ascendente && diff <= 0) {
				if (diff > -200) {
					this.getFuncion("f3").encender();
					return;
				} else {
					parpadear = true;
				}
			}
		}
		if (parpadear) {
			this.getFuncion("f3").parpadear();
			return;
		}
		this.getFuncion("f3").apagar();
	}

	// public void vehicleStop(RoadPlace rp) {
	// try {
	// String message = buildMessage("VEHICLE_OUT", "PrivateUsage");
	// this.publisher.publish(this.baseTopic + "/road/" + this.rp.getRoad()
	// +"/traffic", message);
	// subscriber.unsubscribe(this.baseTopic + "/road/"+this.rp.getRoad()+"/info");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

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

	// private String buildMessage(String state) {
	// JSONObject completeMessage = new JSONObject();
	// try {
	// completeMessage.put("accion", state);
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	// return completeMessage.toString();
	// }

	// public RoadPlace getCurrentPlace() {
	// return rp;
	// }

	// public void changeKm(int km) {
	// this.getCurrentPlace().setKm(km);
	// }

	// public void getIntoRoad(String road, int km) {
	// this.getCurrentPlace().setRoad(road);
	// this.getCurrentPlace().setKm(km);
	// }

	// public void notifyIncident(String incidentType) {
	// if ( this.notifier == null )
	// return;

	// this.notifier.alert(this.getSmartCarID(), incidentType,
	// this.getCurrentPlace());

	// }

}
