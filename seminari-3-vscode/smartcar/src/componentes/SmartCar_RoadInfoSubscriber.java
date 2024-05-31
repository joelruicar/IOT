package componentes;


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
		
		super.messageArrived(topic, message);			// esto muestra el mensaje por pantalla ... comentar para no verlo
		String payload = new String(message.getPayload());

		JSONObject obj = new JSONObject(payload);

		if(obj.getString("type").equals("TRAFFIC_SIGNAL")){
			JSONObject msg = obj.getJSONObject("msg");
			if(msg.getString("signal-type").equals("SPEED_LIMIT")){
				try{
					int value = msg.getInt("value");
					MySimpleLogger.trace(this.clientId, "| Vehicle speed limit change to " + value + " km/h");
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}

		if(obj.getString("type").equals("ROAD_INCIDENT")){
			JSONObject msg = obj.getJSONObject("msg");
			if(msg.getString("incident-type").equals("TRAFFIC_ACCIDENT")){
				try{
					MySimpleLogger.trace(this.clientId, "| Accidente: " + msg.getString("description") 
					                                                    + " ; Inicio(km): " + msg.getInt("starting-position")
																		+ " ; Fin(km)"      + msg.getInt("end-position"));
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		
		// PROCESS THE MESSGE
		// topic - contains the topic
		// payload - contains the message

	}

	

}
