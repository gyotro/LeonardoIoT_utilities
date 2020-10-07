import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.ContentType
import wslite.rest.RESTClient
import wslite.rest.RESTClientException

/*
 per la documentazione di groovy-wslite: https://github.com/jwagenleitner/groovy-wslite
 */

String IDSensorTypeH = "aa456112-1775-4f05-8196-79998c343e9b"
String IDSensorTypeS = "e0af9c88-53a6-4d43-ae84-8a62c165211a"
String IDSensorTypeS1 = "4d841ada-db44-4ea6-95d6-bfbe50f7d92e"
String IDSensorTypeS2 = "c1e87027-270c-4da0-9f5c-e3a40dd73484"
String IDSensorTypeL = "0a6b4238-6d85-4f90-8b94-5f0ce9632e67"

RESTClient client = new RESTClient( InstanceParameter.sURLIoTS )
/*
RESTClient clientH = new RESTClient("https://46600771-8c98-4233-9437-783108b5d21f.eu10.cp.iot.sap/46600771-8c98-4233-9437-783108b5d21f/iot/core/api/v1/tenant/1")
RESTClient clientS = new RESTClient("https://46600771-8c98-4233-9437-783108b5d21f.eu10.cp.iot.sap/46600771-8c98-4233-9437-783108b5d21f/iot/core/api/v1/tenant/1")
RESTClient clientL = new RESTClient("https://46600771-8c98-4233-9437-783108b5d21f.eu10.cp.iot.sap/46600771-8c98-4233-9437-783108b5d21f/iot/core/api/v1/tenant/1/sensorTypes/$IDSensorTypeL/capabilities")
*/

//client.authorization = new HTTPBasicAuthorization("dev", "!d3Vu5r_")
def auth =  InstanceParameter.IoTS_Pwd
def authEncoded = Base64.getEncoder().encodeToString( auth.getBytes() )
def path = "/capabilities"
def pathSensorH = "/sensorTypes/$IDSensorTypeH/capabilities"
def pathSensorS = "/sensorTypes/$IDSensorTypeS/capabilities"
def pathSensorS1 = "/sensorTypes/$IDSensorTypeS1/capabilities"
def pathSensorS2 = "/sensorTypes/$IDSensorTypeS2/capabilities"
def pathSensorL = "/sensorTypes/$IDSensorTypeL/capabilities"

Map<String, ?> restParam = new LinkedHashMap<>()
Map<String, ?> restParamH = new LinkedHashMap<>()
Map<String, ?> restParamS = new LinkedHashMap<>()
Map<String, ?> restParamS1 = new LinkedHashMap<>()
Map<String, ?> restParamS2 = new LinkedHashMap<>()
Map<String, ?> restParamL = new LinkedHashMap<>()
Map<String, String> restQuery = new LinkedHashMap<>()
Map<String, String> restAuthorization = new LinkedHashMap<>()

restAuthorization.Authorization = "Basic $authEncoded"
restQuery.skip = "0"
restQuery.top = "2"
restQuery.filter = ["name eq FE-660", "name eq FE-242"]
restParam.headers = restAuthorization
restParam.path = path
restParamH.headers = restAuthorization
restParamH.path = pathSensorH
restParamS.headers = restAuthorization
restParamS.path = pathSensorS
restParamS1.headers = restAuthorization
restParamS1.path = pathSensorS1
restParamS2.headers = restAuthorization
restParamS2.path = pathSensorS2
restParamL.headers = restAuthorization
restParamL.path = pathSensorL
String sName = 'PowerFactorPF3'
String sAltId = '18520'
def builder = new JsonBuilder()
builder {
    "alternateId" (sAltId)
    "name" ( "IG_$sName"+'0' )
    "properties"(
                [
                        "dataType": "double",
                        "name": "I_$sName"
                ],
            [
                    "dataType": "boolean",
                    "name": "I_Quality"
            ]

    )

}

println(builder.toPrettyString())

/*
String jsonReq = "{\n" +
        "  \"alternateId\": \"18442\",\n" +
        "  \"name\": \"testJsonCapa\",\n" +
        "  \"properties\": [\n" +
        "    {\n" +
        "      \"dataType\": \"double\",\n" +
        "      \"name\": \"value\" " +
        "    },\n" +
        " {\n" +
            "      \"dataType\": \"boolean\",\n" +
                    "      \"name\": \"quality\" " +
                    "    }"
        "  ]\n" +
        "}"
 */

String jsonReq = builder.toPrettyString()
Closure cData = {
    "type" ContentType.JSON
    "bytes" jsonReq.getBytes()
}
//restParam.query = restQuery
def response
try {
    //response = client.get(path: path)
 /*   response = client
            .post(restParam,
                    {
                            type ContentType.JSON
                            bytes jsonReq.getBytes()
                            }
                ) */
    response = client
            .post(restParam, cData)

    if( response.statusCode == 200 ) {
        println("Risposta OK!")
        String sResponse = JsonOutput.prettyPrint(response.getContentAsString())
        JsonSlurper jsonSlurper = new JsonSlurper();
        Map jResp = jsonSlurper.parseText(sResponse)
        String sId = jResp.id
        println("Id assegnato alla capability: $sId")
        Thread.sleep(1000)

        def builder2 = new JsonBuilder()
        builder2 {
            "id"(sId)
            "type"("measure")

        }

        String jsonReq2 = builder2.toPrettyString()
        Closure cData2 = {
            "type" ContentType.JSON  // se é valorizzato il campo inferiore questo tag viene ignorato
            "bytes" jsonReq2.getBytes()
        }
        try {

            response = client
                    .post(restParamH, cData2)

            if (response.statusCode == 200)
                println("Creazione su Sensore Heller OK!")

            Thread.sleep(1000)

            }catch(Exception e)
            {
                println("Errore su chiamata servizio sensori Heller")
            }

        try {
            response = client
                    .post(restParamS, cData2)

            if (response.statusCode == 200)
                println("Creazione su Sensore Starrag 2004 OK!")

            Thread.sleep(2000)

        }catch(Exception e)
        {
            println("Errore su chiamata servizio sensori Starrag 2004")
        }

        try {
            response = client
                    .post(restParamS1, cData2)

            if (response.statusCode == 200)
                println("Creazione su Sensore Starrag normale OK!")

            Thread.sleep(1000)

        }catch(Exception e)
        {
            println("Errore su chiamata servizio sensori Starrag normale")
        }

        try {
            response = client
                    .post(restParamS2, cData2)

            if (response.statusCode == 200)
                println("Creazione su Sensore Starrag 2003 OK!")

            Thread.sleep(1000)

        }catch(Exception e)
        {
            println("Errore su chiamata servizio sensori Starrag 2003")
        }

        try {
        response = client
                    .post(restParamL, cData2)

            if (response.statusCode == 200)
                println("Creazione su Sensore Liechti OK!")


        }catch(Exception e)
        {
            println("Errore su chiamata servizio sensori Liechti")
        }

    }

}catch(Exception e){
    println("Errore su chiamata servizio Capability")
}

println("Programma Terminato!")


