package dispositivo.iniciador;

import dispositivo.api.mqtt.FunctionPublisher_APIMQTT;
import dispositivo.componentes.Dispositivo;
import dispositivo.componentes.Funcion;
import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

public class DispositivoIniciador {

	public static void main(String[] args) {
		
		if ( args.length < 4 ) {
			System.out.println("Usage: java -jar dispositivo.jar device deviceIP rest-port mqttBroker");
			System.out.println("Example: java -jar dispositivo.jar ttmi050 ttmi050.iot.upv.es 8182 tcp://ttmi008.iot.upv.es:1883");
			return;
		}

		String deviceId = args[0];
		String deviceIP = args[1];
		String port = args[2];
		String mqttBroker = args[3];
		
		IDispositivo d = Dispositivo.build(deviceId, deviceIP, Integer.valueOf(port), mqttBroker);

		FunctionPublisher_APIMQTT publisher_APIMQTT = FunctionPublisher_APIMQTT.build( d, mqttBroker);

		d.deshabilitar();

		// Añadimos funciones al dispositivo
		IFuncion f1 = Funcion.build("f1", FuncionStatus.OFF);
		d.addFuncion(f1);

		IFuncion f2 = Funcion.build("f2", FuncionStatus.OFF);
		d.addFuncion(f2);

		IFuncion f3 = Funcion.build("f3", FuncionStatus.BLINK); // Ejercicio 1
		d.addFuncion(f3);

		// Arrancamos el dispositivo
		d.iniciar();


		publisher_APIMQTT.iniciar();
		
		
		String dispositivoId = d.getId();
		String commando = "{'accion':'deshabilitar'}";

		publisher_APIMQTT.publish_status(dispositivoId, commando);

}

}
