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
public class TelephoneNumberAddressAdd extends AbstractWebservice<TelephoneNumberAddressAddResponse> {
    public static final String PARAM_ORGANIZATION_ID = "OrganizationId";
    public static final String PARAM_USERNAME        = "Username";
    public static final String PARAM_DOMAIN          = "Domain";
    public static final String PARAM_NAME            = "Name";

    public TelephoneNumberAddressAdd() {
	super.action = "TelephoneNumberAddressAdd";
    }

    @Override
    public TelephoneNumberAddressAddResponse sendRequest(Map<String, String> parameters) throws IOException {
	return super.sendRequest(parameters);
    }

    @Override
    public TelephoneNumberAddressAddResponse mapJson(String jsonString) {
	TelephoneNumberAddressAddResponse response = new TelephoneNumberAddressAddResponse();
	try {
	    JSONObject serviceObj = parseResultAndError(response, jsonString, TelephoneNumberAddressAddResponse.class.getSimpleName());
	    if (serviceObj == null) {
		return response;
	    }
	} catch (Exception e) {
	    throw new IllegalStateException("Error processing TelephoneNumberAddressAdd response:", e);
	}
	return response;
    }
}
