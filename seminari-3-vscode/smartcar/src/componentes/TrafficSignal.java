package componentes;

// import utils.MySimpleLogger;

import java.time.Instant;  

// import org.eclipse.paho.client.mqttv3.MqttClient;
// import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
// import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

public class TrafficSignal {


	protected String brokerURL = null;

	protected String trafficSignalID = null;
	protected int startPosition = 0;
	protected int endPosition = 0;

	protected RoadPlace rp = null;	// simula la ubicación actual del vehículo
	protected SmartCar_RoadInfoSubscriber subscriber = null;
	protected SmartCar_InicidentNotifier notifier = null;
	protected MyMqttClient publisher = null;
	protected String baseTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna";
	public TrafficSignal(String id, String brokerURL) {
		
		this.setTrafficSignalID(id);
		this.brokerURL = brokerURL;
		
		this.publisher = new MyMqttClient(id+".traffic",this, this.brokerURL);
		publisher.connect();
	}
	
	
	public void setTrafficSignalID(String trafficSignalID) {
		this.trafficSignalID = trafficSignalID;
	}
	
	public String getTrafficSignalID() {
		return trafficSignalID;
	}

	public void setCurrentRoadPlace(RoadPlace rp, String signaltype, String value, int start, int end) {
		if (this.rp == null && rp != null) {
			this.rp = rp;
			try {
				this.startPosition = start;
				this.endPosition = end;

				String message = buildMessage(signaltype, value);
				this.publisher.publish(this.baseTopic + "/road/" + this.rp.getRoad() +"/signals", message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.rp != rp) {
			try {
				this.startPosition = start;
				this.endPosition = end;

				String message = buildMessage("SIGNAL_OUT", "PrivateUsage");
				this.publisher.publish(this.baseTopic + "/road/" + this.rp.getRoad() +"/signals", message);
				this.rp = rp;
				message = buildMessage(signaltype, value);
				this.publisher.publish(this.baseTopic + "/road/" + this.rp.getRoad() +"/signals", message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String buildMessage(String signaltype, String value) {
		JSONObject message = new JSONObject();
		JSONObject completeMessage = new JSONObject();
		Instant timestamp =  Instant.now();
		try {
			completeMessage.put("id", "MSG_" + timestamp.getEpochSecond());
			completeMessage.put("type", "TRAFFIC_SIGNAL");
			completeMessage.put("timestamp", timestamp.getEpochSecond());
			message.put("rt", "traffic-signal");
			message.put("id", this.trafficSignalID);
			message.put("road", this.rp.getRoad().split("s")[0]);
			message.put("road-segment", this.rp.getRoad());
			message.put("signal-type", signaltype);
			message.put("starting-position", this.startPosition);
			message.put("end-position", this.endPosition);
			message.put("value", value);
			completeMessage.put("msg", message);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return completeMessage.toString();
	}

	public RoadPlace getCurrentPlace() {
		return rp;
	}

	public void changeKm(int km) {
		this.getCurrentPlace().setKm(km);
	}
	
	public void getIntoRoad(String road, int km) {
		this.getCurrentPlace().setRoad(road);
		this.getCurrentPlace().setKm(km);
	}
	
	public void notifyIncident(String incidentType) {
		if ( this.notifier == null )
			return;
		
		this.notifier.alert(this.getTrafficSignalID(), incidentType, this.getCurrentPlace());
		
	}

}
