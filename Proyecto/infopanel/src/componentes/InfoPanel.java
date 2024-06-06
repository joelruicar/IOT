package componentes;

// import utils.MySimpleLogger;

import java.time.Instant;  

// import org.eclipse.paho.client.mqttv3.MqttClient;
// import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
// import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;


public class InfoPanel {


	protected String brokerURL = null;
	protected String infoPanelID = null;
	protected String deviceID = null;

	protected RoadPlace rp = null;	// simula la ubicación actual
	protected InfoPanel_RoadInfoSubscriber subscriber = null;
	// protected SmartCar_InicidentNotifier notifier = null;
	protected MyMqttClient publisher = null;
	protected String baseTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna";

	public InfoPanel(String id, String brokerURL, String deviceID) {
		
		// this.setSmartCarID(id);
		this.infoPanelID = id;
		this.brokerURL = brokerURL;
		this.deviceID = deviceID;
		
		// this.notifier = new SmartCar_InicidentNotifier(id + ".incident-notifier", this, this.brokerURL);
		// this.notifier.connect();
		this.publisher = new MyMqttClient(id+".traffic",this, this.brokerURL);
		publisher.connect();
		this.subscriber = new InfoPanel_RoadInfoSubscriber(id, this, brokerURL);
		subscriber.connect();
	}

	public void f(String state, String target){
		System.out.println("State: " + state + "; target: " + target);
		String topic = "dispositivo/" + this.deviceID + "/funcion/" + target + "/comandos";
		String message = buildMessage(state);

		this.publisher.publish(topic, message);
	}
	
	
	// public void setSmartCarID(String smartCarID) {
	// 	this.smartCarID = smartCarID;
	// }
	
	// public String getSmartCarID() {
	// 	return smartCarID;
	// }

	public void setCurrentRoadPlace(RoadPlace rp) {
		// 1.- Si ya teníamos algún suscriptor conectado al tramo de carretera antiguo, primero los desconectamos
		if (this.rp != rp && rp != null && this.rp != null) {
			try {
				subscriber.unsubscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/info");
				this.rp = rp;
				subscriber.subscribe(this.baseTopic + "/road/" + this.rp.getRoad() + "/info");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 2.- Ahora debemos crear suscriptor/es para conocer 'cosas' de dicho tramo de carretra, y conectarlo/s
		// 3.- Debemos suscribir este/os suscriptor/es a los canales adecuados
		else if (this.rp == null && rp != null) {
			this.rp = rp;
			try {
				subscriber.subscribe(this.baseTopic + "/road/"+this.rp.getRoad()+"/info");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public void vehicleStop(RoadPlace rp) {
	// 	try {
	// 		String message = buildMessage("VEHICLE_OUT", "PrivateUsage");
	// 		this.publisher.publish(this.baseTopic + "/road/" + this.rp.getRoad() +"/traffic", message);
	// 		subscriber.unsubscribe(this.baseTopic + "/road/"+this.rp.getRoad()+"/info");
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// }

	private String buildMessage(String state) {
		JSONObject completeMessage = new JSONObject();
		try {
			completeMessage.put("accion", state);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return completeMessage.toString();
	}

	// public RoadPlace getCurrentPlace() {
	// 	return rp;
	// }

	// public void changeKm(int km) {
	// 	this.getCurrentPlace().setKm(km);
	// }
	
	// public void getIntoRoad(String road, int km) {
	// 	this.getCurrentPlace().setRoad(road);
	// 	this.getCurrentPlace().setKm(km);
	// }
	
	// public void notifyIncident(String incidentType) {
	// 	if ( this.notifier == null )
	// 		return;
		
	// 	this.notifier.alert(this.getSmartCarID(), incidentType, this.getCurrentPlace());
		
	// }

}
