package mqtt_Client

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import groovy.json.JsonSlurper
import groovy.transform.Undefined.EXCEPTION
import java.text.ParseException
import mqtt_Client.UsefulParam

import java.util.ArrayList;
import java.util.Arrays;

class MQTT_RunClient implements MqttCallback{
	
	private MqttClient mqttForwardClient;
	private MqttConnectOptions connOpts;
	String brokerAddress = UsefulParam.sBrokerAddress 

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			this.runClient();
		} catch (Exception e) {
			println e.printStackTrace()
		}
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void messageArrived(String topic, MqttMessage message)
	{
		String arrivedMsg = null;
		HashMap msgOut = new HashMap()
		
		if (message.getPayload() != null && !message.getPayload().toString().isEmpty() && !message.isRetained()) {
			arrivedMsg = new String(message.getPayload());
		} else {
			return;
		}

		println("-------------------------------------------------");
		println("| Topic:" + topic);
		println("| Message: " + arrivedMsg);
		println("-------------------------------------------------");
		
		JsonSlurper arrivedJObj = new JsonSlurper()
		try {
			HashMap msgIn = arrivedJObj.parse(arrivedMsg)
		} catch (ParseException e1) {	
			println e1.printStackTrace()
		}
		
		// se non c'è il telemetry data list si esce
		if(!msgIn.containsKey("telemetryDataList")) 
		{
			return;
		}
		
		ArrayList<HashMap<String , ?>> telemetryJArr = msgIn.telemetryDataList
		ArrayList<HashMap<String , ?>> listaJsonOutVector_bk = msgIn.telemetryDataList
		
//		for (HashMap currentJson : telemetryJArr)
//		{
//			currentJson.eachWithIndex{ entry, index -> (entry.getKey("varId") ) ? date : entry.value)) }
//			listaJsonOut.add(listaJsonOut_bk)
//		}
		listaJsonOutVector_bk.add( listaJson2.find{it.varId == 5005} )
		
		

	}
	
	public void runClient() throws InterruptedException {
		
		// Setting up the MQTT client
		connOpts = new MqttConnectOptions();

		connOpts.setCleanSession(true);
		connOpts.setKeepAliveInterval(30);
		connOpts.setAutomaticReconnect(true);

		println "Trying to connect to broker..."
		// Connect to broker
		while (true)
			try {
				MemoryPersistence persistence1 = new MemoryPersistence();
				
				// si crea un MQTT Client che si sottoscrive a tutti i topic dei device di Alleantia e quindi riceve i pacchetti in arrivo da Alleantia
				/*
				 * in realtà questi topic, nonostante arrivino anche sul gateway, non vanno in errore
				 * sul gateway perché il loro TOPIC non è standard (measures/DeviceAletraneId)
				 */
				
				mqttForwardClient = new MqttClient(brokerAddress, MqttClient.generateClientId(), persistence1);
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				connOpts.setKeepAliveInterval(30);
				connOpts.setAutomaticReconnect(true);
				connOpts.setMaxInflight(1000);
				mqttForwardClient.setCallback(this);
				mqttForwardClient.connect(connOpts);

				// subscribe to all topics specified in converterConfig (device/telemetry)
				this.loadSubscriptions();

				println "AENConverterInterceptor connected to " + brokerAddress;

				break;

			} catch (MqttException e) {
				println(e.getCause().toString());
				Thread.sleep(5000);
			}
	}
	
	public void mqttForwardMessage(MqttMessage message, String topic) {
		
				try {
					// Publish message to broker
					mqttForwardClient.publish(topic, message);
				} catch (MqttException e) {
					println e.printStackTrace();
				}
			}
		
			public void disconnectClient() {
				try {
					mqttForwardClient.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
			public void loadSubscriptions(String sTopicToSubscribe) {
		
//				String configJsonStr = InterceptorActivator.configJson.toString();
//				JSONObject configJson = null;
//				JSONArray topicsToSubscribe = null;
//				try {
//					configJson = (JSONObject) new JSONParser().parse(configJsonStr);
//					topicsToSubscribe = (JSONArray) new JSONParser().parse(configJson.get("devicesTopics").toString());
//				} catch (ParseException e1) {
//					log.error("Error while parsing json", e1);
//				}
//		
				ArrayList<String> topicFiltersAL = new ArrayList<String>();
				ArrayList<Integer> QoSAL = new ArrayList<Integer>();
		
//				for (int i = 0; i < topicsToSubscribe.toArray().length; i++) {
//					// i topic in uscita da alleantia avranno i topic devideAlternateId/telemetry (in cui ci saranno sia quelli di telemetria, sia gli allarm)
//					String topicToSubscribe = topicsToSubscribe.get(i) + "/telemetry";
//					log.info(topicToSubscribe);
//					topicFiltersAL.add(topicToSubscribe);
//					QoSAL.add(0);
//				}
				
				sTopicToSubscribe = sTopicToSubscribe + "/telemetry"
				topicFiltersAL.add(sTopicToSubscribe)
				QoSAL.add(0)
				
				// si converte da Lista ad array di oggetti e poi da array di oggetti in array di stringhe
				Object[] objectTopicList = topicFiltersAL.toArray();
				String[] topicFilters = Arrays.copyOf(objectTopicList, objectTopicList.length, String[].class);
		
				int[] qosFilters = new int[QoSAL.size()];
				// si crea un array di int per la QoS
				for (int i = 0; i < QoSAL.size(); i++) {
					qosFilters[i] = QoSAL.get(i);
				}
		
				try {
					mqttForwardClient.subscribe(topicFilters, qosFilters);
				} catch (MqttException e) 
				{
					e.printStackTrace();
				}
			}
			
			
}
