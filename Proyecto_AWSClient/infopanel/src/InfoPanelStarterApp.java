import componentes.InfoPanel;

public class InfoPanelStarterApp {
	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
			System.out.println("Usage: InfoPanelStarterApp <infoPanelID> <brokerURL> <deviceID>");
			System.exit(1);
		}

		String infoPanelID = args[0];
		// String brokerURL = args[1];
		// String brokerURL = "tcp://d1512621af344ae4bf3b3891ca0eaf8e.s1.eu.hivemq.cloud:8883";
		String deviceID = args[2];

		new InfoPanel(infoPanelID, deviceID);

	}
}
