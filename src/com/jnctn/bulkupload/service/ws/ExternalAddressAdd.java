package com.jnctn.bulkupload.service.ws;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import org.json.simple.JSONObject;
import com.jnctn.bulkupload.model.json.*;

/**
 * Webservice programming interface for adding an extension to to a user's address.
 *
 * @author martin
 */
public class ExternalAddressAdd extends AbstractWebservice<ExternalAddressAddResponse> {
    public static final String PARAM_ORGANIZATION_ID = "OrganizationId";
    public static final String PARAM_USERNAME        = "Username";
    public static final String PARAM_DOMAIN          = "Domain";
    public static final String PARAM_FOREIGN_ADDRESS = "ForeignAddress";

    public ExternalAddressAdd() {
	super.action = "ExternalAddressAdd";
    }

    @Override
    public ExternalAddressAddResponse sendRequest(Map<String, String> parameters) throws IOException {
	return super.sendRequest(parameters);
    }

    @Override
    public ExternalAddressAddResponse mapJson(String jsonString) {
	ExternalAddressAddResponse response = new ExternalAddressAddResponse();
	try {
	    JSONObject serviceObj = parseResultAndError(response, jsonString, ExternalAddressAddResponse.class.getSimpleName());
	    if (serviceObj == null) {
		return response;
	    }
	} catch (Exception e) {
	    throw new IllegalStateException("Error processing ExternalAddressAdd response:", e);
	}
	return response;
    }
}
