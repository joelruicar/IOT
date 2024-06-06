package componentes_old;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import utils.MySimpleLogger;

public class SmartCar_RoadInfoSubscriber extends MyMqttClient {

	protected SmartCar theSmartCar;

	public SmartCar_RoadInfoSubscriber(String clientId, SmartCar smartcar, String MQTTBrokerURL) {
		super(clientId, smartcar, MQTTBrokerURL);
		this.smartcar = smartcar;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		super.messageArrived(topic, message); // esto muestra el mensaje por pantalla ... comentar para no verlo
		String payload = new String(message.getPayload());

		JSONObject obj = new JSONObject(payload);

		if (obj.getString("type").equals("TRAFFIC_SIGNAL")) {
			JSONObject msg = obj.getJSONObject("msg");
			if (msg.getString("signal-type").equals("SPEED_LIMIT")) {
				try {
					int value = msg.getInt("value");
					HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(
									"http://ttmi008.iot.upv.es:8182/segment/" + smartcar.getCurrentPlace().getRoad()))
							.header("Accept", "application/json")
							.header("Content-Type", "application/json")
							.method("GET", HttpRequest.BodyPublishers.noBody())
							.build();
					HttpResponse<String> response = null;
					try {
						response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// System.out.println(response.body());

					JSONObject objres = new JSONObject(response.body());
					// System.out.println("Max Carretera: " + objres.getInt("max-speed"));
					int valuesegment = objres.getInt("max-speed");
					MySimpleLogger.trace(this.clientId, "| Vehicle speed limit change to " + (value < valuesegment ? value : valuesegment) + " km/h");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (obj.getString("type").equals("ROAD_INCIDENT")) {
			JSONObject msg = obj.getJSONObject("msg");
			if (msg.getString("incident-type").equals("TRAFFIC_ACCIDENT")) {
				try {
					MySimpleLogger.trace(this.clientId, "| Accidente: " + msg.getString("description")
							+ " ; Inicio(km): " + msg.getInt("starting-position")
							+ " ; Fin(km)" + msg.getInt("end-position"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// PROCESS THE MESSGE
		// topic - contains the topic
		// payload - contains the message

	}

}
