import componentes.InfoPanel;
import componentes.RoadPlace;

public class InfoPanelStarterApp {
  public static void main(String[] args) throws Exception {

    if ( args.length < 3 )
		{
			System.out.println("Usage: InfoPanelStarterApp <infoPanelID> <brokerURL> <deviceID>");
			System.exit(1);
		}

		String infoPanelID = args[0];
		String brokerURL = args[1];
    String deviceID = args[2];

    InfoPanel infoPanel = new InfoPanel(infoPanelID, brokerURL, deviceID);
    infoPanel.setCurrentRoadPlace(new RoadPlace("R1s1", 0));



  }
}
