import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import wslite.rest.RESTClientException


//RESTClient client = new RESTClient("https://46600771-8c98-4233-9437-783108b5d21f.eu10.cp.iot.sap/46600771-8c98-4233-9437-783108b5d21f/iot/core/api/v1/tenant/1")
//client.authorization = new HTTPBasicAuthorization("dev", "!d3Vu5r_")
RESTClient client = new RESTClient("https://devhanae98035808.eu2.hana.ondemand.com/integration/EBilling/Model/services/ebilling.xsodata")
//client.authorization = new HTTPBasicAuthorization("SCPI_USER_EBILLING", "Password@1") // l'autenticazione si mapperà in basso come mappa
def auth = "SCPI_USER_EBILLING:Password@1"
def authEncoded = Base64.getEncoder().encodeToString(auth.getBytes())
def path = "/CompanySet"
Map<String, ?> restParam = new LinkedHashMap<>()
Map<String, String> restQuery = new LinkedHashMap<>()
Map<String, String> restAuthorization = new LinkedHashMap<>()
restAuthorization.Authorization = "Basic $authEncoded"
/*
restQuery.skip = "0"
restQuery.top = "2"
restQuery.filter = "name eq FE-660 or name eq FE-242"
 */
restQuery.$format = "json" // per gli odata le query vanno precedute da $
restParam.headers = restAuthorization
restParam.path = path
restParam.query = restQuery
def response
try {
    //response = client.get()
    response = client.get(restParam)

    if( response.statusCode == 200 )
    {
        println( JsonOutput.prettyPrint( response.getContentAsString() ) )
        def jsonMap = new JsonSlurper().parseText(response.getContentAsString())
        ArrayList<Map> results = jsonMap.d.results
        Map value = results.find {it.COMPANY == "2320"}
        println(value)
    }


} catch (RESTClientException e) {
    println("Error $response.statusCode")
}

/*
RESTClient client = new RESTClient("https://postman-echo.com")
def path = "/get"
def response
try {
    response = client.get(path: path)
    if( response.statusCode == 200 )
        println( response.getContentAsString() )
} catch (RESTClientException e) {
    println("Error $response.statusCode")
}
*/
