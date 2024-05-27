package dispositivo.iniciador;

import dispositivo.api.mqtt.FunctionPublisher_APIMQTT;
import dispositivo.componentes.Dispositivo;
import dispositivo.componentes.Funcion;
import dispositivo.interfaces.Configuracion;
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

		FunctionPublisher_APIMQTT publisher_APIMQTT = FunctionPublisher_APIMQTT.build(  mqttBroker);
		
		IDispositivo d = Dispositivo.build(deviceId, deviceIP, Integer.valueOf(port), mqttBroker, publisher_APIMQTT);

		d.deshabilitar();

		// Añadimos funciones al dispositivo
		Funcion f1 = Funcion.build("f1", FuncionStatus.OFF);
		d.addFuncion(f1);

		Funcion f2 = Funcion.build("f2", FuncionStatus.OFF);
		d.addFuncion(f2);

		Funcion f3 = Funcion.build("f3", FuncionStatus.BLINK); // Ejercicio 1
		d.addFuncion(f3);

		// Arrancamos el dispositivo
		d.iniciar();

		// Arrancar el publisher
		publisher_APIMQTT.iniciar(d.getId());
		
		// Ej 7
		String topic7 = Configuracion.TOPIC_BASE + "dispositivo/" + d.getId() + "/comandos";
		String commando7 = "{'accion':'deshabilitar'}";
		
		publisher_APIMQTT.publish_status(topic7, commando7);

		// Ej 8
		String topic8 = Configuracion.TOPIC_BASE + "dispositivo/" + d.getId() + "/funcion/" + f1.getId() + "/comandos";
		String commando8 = "{'accion':'parpadear'}";

		publisher_APIMQTT.publish_status(topic8, commando8);

		// Ej 9 - Done @Dispositivo_APIMQTT.java - messageArrived

		// Ej 10
		

	}
}
