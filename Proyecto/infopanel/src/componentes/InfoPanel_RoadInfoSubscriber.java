package componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

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
				infoPanel.getFuncion("f1").apagar();
				infoPanel.AWS_Report("f1", "apagar");
			}
			else if (msg.getString("status").toUpperCase().equals("Limited_Manouvers".toUpperCase())) {
				infoPanel.getFuncion("f1").parpadear();
				infoPanel.AWS_Report("f1", "parpadear");
			}
			else if (msg.getString("status").toUpperCase().equals("No_Manouvers".toUpperCase()) || 
				     msg.getString("status").toUpperCase().equals(   "Collapsed".toUpperCase())) {
				infoPanel.getFuncion("f1").encender();
				infoPanel.AWS_Report("f1", "encender");
			}
		}
		else if (obj.getString("type").equals("ACCIDENT")) {
			JSONObject msg = obj.getJSONObject("msg");
			if (msg.getString("event").toUpperCase().equals("OPEN".toUpperCase())) {
				infoPanel.setAccidente(msg.getString("id"));
			}
			else if (msg.getString("event").toUpperCase().equals("CLOSE".toUpperCase())) {
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
}
