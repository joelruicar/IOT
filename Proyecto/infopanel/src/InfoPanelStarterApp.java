import componentes.Funcion;
import componentes.InfoPanel;
import componentes.RoadPlace;
import interfaces.FuncionStatus;

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
    // infoPanel.setCurrentRoadPlace(new RoadPlace("R1s1", 300));
    infoPanel.setCurrentRoadPlace(new RoadPlace("R1s2d", 300));

    Funcion f1 = Funcion.build("f1", FuncionStatus.OFF);
		infoPanel.addFuncion(f1);

		Funcion f2 = Funcion.build("f2", FuncionStatus.OFF);
		infoPanel.addFuncion(f2);

		Funcion f3 = Funcion.build("f3", FuncionStatus.OFF);
		infoPanel.addFuncion(f3);

  }
}
