package componentes;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import interfaces.IFuncion;
import utils.MySimpleLogger;

public class InfoPanel_RoadInfoSubscriber extends MyMqttClient {

	protected InfoPanel infoPanel;

	public InfoPanel_RoadInfoSubscriber(String clientId, InfoPanel infoPanel, String MQTTBrokerURL) {
		super(clientId, infoPanel, MQTTBrokerURL);
		this.infoPanel = infoPanel;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// super.messageArrived(topic, message);
		String payload = new String(message.getPayload());
		JSONObject obj = new JSONObject(payload);

		if (obj.getString("type").equals("ROAD_STATUS")) {
			JSONObject msg = obj.getJSONObject("msg");
			if (msg.getString("status").toUpperCase().equals("Mostly_Free_Flow".toUpperCase()) || 
				msg.getString("status").toUpperCase().equals(       "Free_Flow".toUpperCase())) {
				// infoPanel.f("apagar", "f1");
				infoPanel.getFuncion("f1").apagar();
			}
			else if (msg.getString("status").toUpperCase().equals("Limited_Manouvers".toUpperCase())) {
				// infoPanel.f("parpadear", "f1");
				infoPanel.getFuncion("f1").parpadear();
			}
			else if (msg.getString("status").toUpperCase().equals("No_Manouvers".toUpperCase()) || 
				     msg.getString("status").toUpperCase().equals(   "Collapsed".toUpperCase())) {
				// infoPanel.f("encender", "f1");
				infoPanel.getFuncion("f1").encender();
			}
		}
		else if (obj.getString("type").equals("ACCIDENT")) {
			JSONObject msg = obj.getJSONObject("msg");
			if (msg.getString("event").toUpperCase().equals("OPEN".toUpperCase())) {
				infoPanel.setAccidente(msg.getString("id"));
			}
			else if (msg.getString("event").toUpperCase().equals("CLOSE".toUpperCase())) {
				// infoPanel.f("parpadear", "f1");
				infoPanel.removeAccidente(msg.getString("id"));
			}
		}
		else if (obj.getString("type").equals("TRAFFIC")) {
			JSONObject msg = obj.getJSONObject("msg");
			if (msg.getString("vehicle-role").toUpperCase().equals("Ambulance".toUpperCase())) {
				if (msg.getString("action").toUpperCase().equals("VEHICLE_IN".toUpperCase()) 
				|| 	msg.getString("action").toUpperCase().equals("CHECK_IN".toUpperCase()))
					
					infoPanel.setEspeciales(msg.getString("vehicle-id"), msg.getInt("position"));
				else if (msg.getString("action").toUpperCase().equals("VEHICLE_OUT".toUpperCase()))
					infoPanel.removeEspeciales(msg.getString("vehicle-id"));
				
			}
		}
		
	}

	// public SmartCar_RoadInfoSubscriber(String clientId, InfoPanel smartcar, String MQTTBrokerURL) {
	// 	super(clientId, smartcar, MQTTBrokerURL);
	// 	this.smartcar = smartcar;
	// }

	// @Override
	// public void messageArrived(String topic, MqttMessage message) throws Exception {

	// 	super.messageArrived(topic, message); // esto muestra el mensaje por pantalla ... comentar para no verlo
	// 	String payload = new String(message.getPayload());

	// 	JSONObject obj = new JSONObject(payload);

	// 	if (obj.getString("type").equals("TRAFFIC_SIGNAL")) {
	// 		JSONObject msg = obj.getJSONObject("msg");
	// 		if (msg.getString("signal-type").equals("SPEED_LIMIT")) {
	// 			try {
	// 				int value = msg.getInt("value");
	// 				HttpRequest request = HttpRequest.newBuilder()
	// 						.uri(URI.create(
	// 								"http://ttmi008.iot.upv.es:8182/segment/" + smartcar.getCurrentPlace().getRoad()))
	// 						.header("Accept", "application/json")
	// 						.header("Content-Type", "application/json")
	// 						.method("GET", HttpRequest.BodyPublishers.noBody())
	// 						.build();
	// 				HttpResponse<String> response = null;
	// 				try {
	// 					response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
	// 				} catch (IOException e) {
	// 					e.printStackTrace();
	// 				} catch (InterruptedException e) {
	// 					e.printStackTrace();
	// 				}
	// 				// System.out.println(response.body());

	// 				JSONObject objres = new JSONObject(response.body());
	// 				// System.out.println("Max Carretera: " + objres.getInt("max-speed"));
	// 				int valuesegment = objres.getInt("max-speed");
	// 				MySimpleLogger.trace(this.clientId, "| Vehicle speed limit change to " + (value < valuesegment ? value : valuesegment) + " km/h");
	// 			} catch (Exception e) {
	// 				e.printStackTrace();
	// 			}
	// 		}
	// 	}

	// 	if (obj.getString("type").equals("ROAD_INCIDENT")) {
	// 		JSONObject msg = obj.getJSONObject("msg");
	// 		if (msg.getString("incident-type").equals("TRAFFIC_ACCIDENT")) {
	// 			try {
	// 				MySimpleLogger.trace(this.clientId, "| Accidente: " + msg.getString("description")
	// 						+ " ; Inicio(km): " + msg.getInt("starting-position")
	// 						+ " ; Fin(km)" + msg.getInt("end-position"));
	// 			} catch (Exception e) {
	// 				e.printStackTrace();
	// 			}
	// 		}
	// 	}

	// 	// PROCESS THE MESSGE
	// 	// topic - contains the topic
	// 	// payload - contains the message

	// }

}
