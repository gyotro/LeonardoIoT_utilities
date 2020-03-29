package mqtt_Client

import groovy.json.JsonSlurper

import java.util.Map.Entry
import java.util.function.Consumer
import java.util.stream.Stream
import groovy.json.JsonOutput
import groovy.json.JsonBuilder

//import org.json.JSONArray
//import org.json.JSONObject

def filePath = UsefulParam.sDirInput + UsefulParam.sFileName
def file = new File(filePath)
def jsonString = file.getText()
String date
//def jsonarray = new org.json.JSONArray(jsonStr)
//for (int i = 0; i < jsonarray.length(); i++) {
//	def jsonobject = jsonarray.getjsonbject(i)
//	
//	}	

def TelemetryJson = new JsonSlurper().parseText( jsonString )
def listaJson = TelemetryJson.get("telemetryDataList")
ArrayList<LinkedHashMap> listaJson2 = TelemetryJson.telemetryDataList
ArrayList<LinkedHashMap<String, Object>> listaJsonOut = new ArrayList<LinkedHashMap<String, Object>>()
ArrayList<LinkedHashMap<String, String> > listaJsonOutVector_bk = new ArrayList<LinkedHashMap>()

LinkedHashMap listaJsonOut_bk;
LinkedHashMap msgOut = new LinkedHashMap()



/* per ogni oggetto dell'array recuperiamo il millisUTC
 * e lo cambiamo con il nostro calcolato
 *
 */
//	for (LinkedHashMap currentJson : listaJson) 
//		{
//		date= System.currentTimeMillis() + ""	
////		def obj = millisUTC.get("millisUTC")
//		
////		currentJson.each { entry -> listaJsonOut_bk.put(entry.key, (( ( (String) entry.key).equalsIgnoreCase("millisUTC") ) ? date : entry.value)) }
////		listaJsonOut_bk.replace("millisUTC", date)
//		
//		currentJson.replace("millisUTC", date)	
//		listaJsonOut.add(currentJson)
//	}
	
for (LinkedHashMap currentJson : listaJson)
		{
		date = ( System.currentTimeMillis() - UsefulParam.delay )  + ""
		listaJsonOut_bk = new LinkedHashMap()
		currentJson.each { entry -> listaJsonOut_bk.put(entry.key, (( ( (String) entry.key).equalsIgnoreCase("millisUTC") ) ? date + "" : entry.value)) }
		listaJsonOut.add(listaJsonOut_bk)
	}
//	
//	listaJson2.eachWithIndex{ item, index-> item.each { it -> it.each {  } } }
//	
//	item.each{ it -> { ( Entry mapEntry : it.entrySet())
//		( String )mapEntry.getKey() == "varId" && (Integer) mapEntry.getValue() == 5005)
//	   }

//ArrayList<Map<String, Object>> filteredList = listaJson2.stream().filter( map -> (Integer) map.get("ID") >= 1 && !"cc".equals(map.get("Name"))).collect(Collectors.toList());
//	for (LinkedHashMap<String, Object> currentJson2 : listaJson2)
//	{
//		for( Map.Entry<String, Object> entry : currentJson2.entrySet() )
//		{
//			if( entry.key == "varId" && (Integer) entry.value == 6121)
//				listaJsonOutVector_bk.add(currentJson2)
//		}
//	}

// listaJsonOutVector_bk.add( listaJson2.find{it.varId == 6121} )

	
//Stream<LinkedHashMap<String, Object>> filteredList = listaJson2.stream().filter(  )

//date = (String) System.currentTimeMillis()
// Da completare
//	listaJson2.each { item -> listaJsonOutVector_bk.add( item.each { key, value -> 
//	listaJsonOut_bk.put(key, ( ( ( (String) key).equalsIgnoreCase("millisUTC") ) ? "testRep" : value) ) } ) }
	
//	listaJson2.forEach( mappa -> { mappa.replace } ) { it -> listaJsonOutVector_bk.add( it.each { key, value ->
//	listaJsonOut_bk.put(key, ( ( ( (String) key).equalsIgnoreCase("millisUTC") ) ? "testRep" : value) ) } ) }

def devSn= TelemetryJson.get("devSn")
def onTime= TelemetryJson.get("onTime") 
def ontTimeMillisUTC = TelemetryJson.get("millisUTC")

/*
 * recuperare gli altri dati: ontTimeMillisUTC, onTime, devSn e costruire il JSON Finale
 */

def date1= System.currentTimeMillis() + ""

LinkedHashMap mapOut= new LinkedHashMap()

mapOut.put("telemetryDataList", listaJsonOut)
mapOut.put("devSn", devSn)
mapOut.put("onTime", onTime)
mapOut.put("ontTimeMillisUTC", date1)

def jsout = new JsonOutput()
//def js = jsout.toJson(mapOut) + ""

String sJout = JsonOutput.prettyPrint((String) jsout.toJson(mapOut))

//JsonOutput jsOutAux = new JsonOutput(mapOut)

//JsonBuilder jsOut= new JsonBuilder()

//File newFile = new File("C:/Users/InnovatesApp/Desktop/msg_2.json")
//newFile.write(sJout, "utf-8")

MQTT_IngectMessages.MQTT_ClientSend("3/telemetry", sJout)



