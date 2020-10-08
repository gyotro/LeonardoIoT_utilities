import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import wslite.http.HTTPMethod
import wslite.rest.ContentType
import wslite.rest.RESTClient
import wslite.rest.Response

/*
 per la documentazione di groovy-wslite: https://github.com/jwagenleitner/groovy-wslite
 */

RESTClient client = new RESTClient( InstanceParameter.sURLToken )
def authEncoded = Base64.getEncoder().encodeToString(( "${InstanceParameter.client_id}:${InstanceParameter.client_secret}" ).getBytes() )

println(" Utenza codificata: $authEncoded")

Map<String, ?> restParam = new LinkedHashMap<>()
Map<String, String> restHeaders = new LinkedHashMap<>()
Map<String, String> restForm= new LinkedHashMap<>()
restHeaders.Authorization = "Basic $authEncoded"
restHeaders.put('Content-Type', 'application/x-www-form-urlencoded')
restForm.grant_type = "client_credentials"
restForm.response_type = 'token'
//restParam.form = restForm

def path = "/token"
restParam.path = path
restParam.headers = restHeaders

println("Parametri per invocazione: $restParam")

Closure cData = {
    "type"  ContentType.URLENC
    "urlenc" restForm
}

Response response
Map jResp

try {
      response = client?.post( restParam, cData )

    /*
     Metodo alternativo saltando il primo passaggio
     def response = client
            .executeMethod( HTTPMethod.POST, restParam, cData )
    */
    if (response?.statusCode == 200) {
        println("Risposta OK!")
        String sResponse = JsonOutput.prettyPrint(response.getContentAsString())
        JsonSlurper jsonSlurper = new JsonSlurper();
        jResp = jsonSlurper.parseText(sResponse)
//            String sId = jResp.id
        println("Response: ${jsonSlurper.parseText(sResponse)}")

    }
}catch(Exception e)
{
    println("Errore su chiamata servizio Authentication: ${e.printStackTrace()}")
}

String sToken = jResp.access_token
String sTokenType = jResp.token_type

println("Token: ${sTokenType} ${sToken}")