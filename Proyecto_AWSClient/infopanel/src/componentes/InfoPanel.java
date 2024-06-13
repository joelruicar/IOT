package componentes;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

import java.util.UUID;

import utils.MySimpleLogger;

public class InfoPanel {

	protected static String clientEndpoint = "a1se7t1dcq6xyp-ats.iot.us-east-1.amazonaws.com"; // replace <prefix> and
																								// <region> with your
																								// own
	protected static String clientId = "IoTDeviceClient-" + UUID.randomUUID().toString(); // replace with your own
																							// client ID. Use unique
																							// client IDs for concurrent
																							// connections.
	protected static String certsDir = "certs/";
	protected static String certID = "fd16159d548a1cdea892f05f82612b34d25387953d7f851d9d74e8a6aafe01f7";
	protected static String certificateFile = certsDir + certID + "-certificate.pem.crt"; // X.509 based certificate
																							// file
	protected static String privateKeyFile = certsDir + certID + "-private.pem.key"; // PKCS#1 or PKCS#8 PEM encoded
																						// private key file
	protected static String loggerId = "my-aws-iot-thing";
	AWSIotQos qos = null;
	AWSIotMqttClient client = null;

	public InfoPanel(String id, String deviceID) {

		this.client = initClient();

		// CONNECT CLIENT TO AWS IOT MQTT
		// optional parameters can be set before connect()
		this.qos = AWSIotQos.QOS0;
		try {
			client.connect();
			MySimpleLogger.info(loggerId, "Client Connected to AWS IoT MQTT");

		} catch (AWSIotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String topic = "f2";
		subscribe(client, topic, qos);

	}

	public static AWSIotMqttClient initClient() {

		// SampleUtil.java and its dependency PrivateKeyReader.java can be copied from
		// the sample source code.
		// Alternatively, you could load key store directly from a file - see the
		// example included in this README.
		KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
		AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

		return client;
	}

	public static void subscribe(AWSIotMqttClient client, String topic, AWSIotQos qos) {
		
		
		AWSIoT_TopicHandler theTopic = new AWSIoT_TopicHandler(topic, qos);
		try {
			client.subscribe(theTopic);
			MySimpleLogger.info(loggerId, "... SUBSCRIBED to TOPIC: " + topic);
		} catch (AWSIotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
