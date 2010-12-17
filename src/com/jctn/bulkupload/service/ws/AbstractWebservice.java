package com.jctn.bulkupload.service.ws;

import java.util.Map;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.jctn.bulkupload.model.json.AbstractJSONResponse;

/**
 * @author martin
 */
public abstract class AbstractWebservice<T extends AbstractJSONResponse> implements JsonMapper<T> {
    /** Name for parameter for the session id. **/
    public  static final String PARAM_SESSION_ID        = "SessionId";

    private static final String DEFAULT_RESPONSE_FORMAT = "json";
    private static final String BASE_SERVICE_URL        = "https://www.jnctn.com/restapi";
    private static final String PARAM_OUTPUT            = "Output";
    private static final String PARAM_ACTION            = "Action";

    private HttpConnector httpConnector;
    protected String sessionId;
    protected String action;

    public String getAction() {
	return action;
    }

    public void setAction(String action) {
	this.action = action;
    }
    
    public String getSessionId() {
	return sessionId;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
    }

    public void setHttpConnector(HttpConnector httpConnector) {
	this.httpConnector = httpConnector;
    }

    /**
     * Sends a RESTful request to the service with the given parameters. Work is
     * delegated to {@link HttpConnector#sendRequest(java.lang.String, java.util.Map) HttpConnector.sendRequest}
     * @param parameters Map of parameter entries
     * @return Response object of type T
     * @throws IOException
     */
    public T sendRequest(Map<String, String> parameters) throws IOException {		
	parameters.put(PARAM_OUTPUT, DEFAULT_RESPONSE_FORMAT);
	parameters.put(PARAM_ACTION, this.action);
	if (httpConnector == null) {
	    httpConnector = new HttpConnector();
	}
	String response = httpConnector.sendRequest(BASE_SERVICE_URL, parameters);
	return mapJson(response);
    }

    protected void parseError(T responseObj, JSONObject parsedFile) {
	JSONObject response  = (JSONObject) parsedFile.get("Response");
	JSONObject context   = (JSONObject) response.get("Context");
	JSONObject request   = (JSONObject) context.get("Request");
	/** Errors **/
	JSONObject errorsObj = (JSONObject) request.get("Errors");
	if (errorsObj == null) {
	    return;
	}
	Object errorObj = errorsObj.get("Error");
	if (errorObj instanceof JSONObject) {
	    JSONObject singleError = (JSONObject) errorObj;
	    responseObj.setErrors(getFirstError(singleError));
	} else {
	    /** There were mulitple errors so just grab the first one and ignore the others for now **/
	    JSONArray manyErrors   = (JSONArray) errorObj;
	    responseObj.setErrors(getFirstError((JSONObject) manyErrors.get(0)));
	}
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getFirstError (JSONObject errorObj) {
	if (errorObj != null) {
	    return (Map<String, String>) errorObj;	   
	}
	return null;
    }

    protected JSONObject parseResultAndError(T responseObj, String jsonString, String serviceName) throws ParseException {
	if (responseObj == null) {
	    throw new IllegalArgumentException("responseObj cannot be null");
	}

	JSONObject resultObj  = null;	
	JSONParser parser     = new JSONParser();
	JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
	parseError(responseObj, jsonObject);
	JSONObject response   = (JSONObject) jsonObject.get("Response");
	JSONObject jresult    = (JSONObject) response.get("Result");
	if (jresult != null) {
	    resultObj = (JSONObject) jresult.get(serviceName);
	}
	return resultObj;
    }
}
