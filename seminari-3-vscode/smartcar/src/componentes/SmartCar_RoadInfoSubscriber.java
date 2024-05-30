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


		// System.out.println("Test1--------------------------------------------------##################" + obj.getString("type"));
		if(obj.getString("type").equals("TRAFFIC_SIGNAL")){

			// System.out.println("Test2--------------------------------------------------########################################################################");
			// JSONObject msg = obj.getString("type");
			JSONObject msg = obj.getJSONObject("msg");
			if(msg.getString("signal-type").equals("SPEED_LIMIT")){
				try{
					int value = msg.getInt("value");
					MySimpleLogger.trace(this.clientId, "| Vehicle speed limit change to " + value + " km/h");
				}
				catch (Exception e){
					e.printStackTrace();
				}
				// String value = msg.getString("value");
				// MySimpleLogger.trace(this.clientId, "| Vehicle speed limit change to " + value + "km/h");
			}
		}
		
		// PROCESS THE MESSGE
		// topic - contains the topic
		// payload - contains the message

	}

	

}
