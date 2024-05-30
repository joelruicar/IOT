import componentes.RoadPlace;
import componentes.SmartCar;
import componentes.TrafficSignal;

public class SmartCarStarterApp {
    public static void main(String[] args) throws Exception {

		if ( args.length < 2 )
		{
			System.out.println("Usage: SmartCarStarterApp <smartCarID> <brokerURL>");
			System.exit(1);
		}

		String smartCarID = args[0];
		String brokerURL = args[1];

        SmartCar sc1 = new SmartCar(smartCarID, brokerURL);
		sc1.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc2 = new SmartCar(smartCarID + "2", brokerURL);
		sc2.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc3 = new SmartCar(smartCarID + "3", brokerURL);
		sc3.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc4 = new SmartCar(smartCarID + "4", brokerURL);
		sc4.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc5 = new SmartCar(smartCarID + "5", brokerURL);
		sc5.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc6 = new SmartCar(smartCarID + "6", brokerURL);
		sc6.setCurrentRoadPlace(new RoadPlace("R1s1", 0));
		
		TrafficSignal tf1 = new TrafficSignal("TL_atR1S3_542", brokerURL);
		tf1.setCurrentRoadPlace(new RoadPlace("R1s1", 0), "SPEED_LIMIT", "20", 0, 0);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		sc4.notifyIncident("Vehiculo con rueda pinchada");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		sc1.vehicleStop(null);
		sc2.vehicleStop(null);
		sc3.vehicleStop(null);
		sc4.vehicleStop(null);
		sc5.vehicleStop(null);
		sc6.vehicleStop(null);

		
    }
}
