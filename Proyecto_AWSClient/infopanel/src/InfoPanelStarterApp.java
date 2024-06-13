import componentes.InfoPanel;

public class InfoPanelStarterApp {
	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
			System.out.println("Usage: InfoPanelStarterApp <infoPanelID> <brokerURL> <deviceID>");
			System.exit(1);
		}

		String infoPanelID = args[0];
		String deviceID = args[2];

		new InfoPanel(infoPanelID, deviceID);

	}
}
