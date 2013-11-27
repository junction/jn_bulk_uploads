package com.jnctn.bulkupload.service.ws;

import java.util.Map;
import java.io.IOException;
import org.json.simple.JSONObject;
import com.jnctn.bulkupload.model.json.AppExtensionAddExtensionResponse;


public class AppExtensionAddExtension extends AbstractWebservice<AppExtensionAddExtensionResponse> {
    public static final String PARAM_ORGANIZATION_ID = "OrganizationId";
    public static final String PARAM_ADDRESS_USERNAME = "AddressUsername";
    public static final String PARAM_EXTENSION = "Extension";
    private static final String PARAM_VISIBILITY = "ExtensionVisibility";
    private static final String DEFAULT_VISIBILITY = "PUBLIC";

    public AppExtensionAddExtension() {
	super.action = "AppExtensionAddExtension";
    }

    @Override
    public AppExtensionAddExtensionResponse sendRequest(Map<String, String> parameters) throws IOException {
	parameters.put(PARAM_VISIBILITY, DEFAULT_VISIBILITY);
	return super.sendRequest(parameters);
    }

    @Override
    public AppExtensionAddExtensionResponse mapJson(String jsonString) {
	AppExtensionAddExtensionResponse response = new AppExtensionAddExtensionResponse();
	try {
	    JSONObject serviceObj = parseResultAndError(response, jsonString, AppExtensionAddExtension.class.getSimpleName());
	    if (serviceObj == null) {
		return response;
	    }
	} catch (Exception e) {
	    throw new IllegalStateException("Error processing AppExtensionAddExtensionResponse response:", e);
	}
	return response;
    }
}
