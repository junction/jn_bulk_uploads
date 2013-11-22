package com.jnctn.bulkupload.service.ws;

import com.jnctn.bulkupload.model.json.SendEmailResponse;
import java.io.IOException;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Webservice programming interface for sending a welcome e-mail
 *
 * @author martin
 */
public class SendEmail extends AbstractWebservice<SendEmailResponse> {
    public static final String PARAM_USERNAME = "Username";
    public static final String PARAM_TYPE = "Type";
    public static final String PARAM_TYPE_DEFAULT_VALUE = "NewUser";

    public SendEmail() {
        super.action = "SendEmail";
    }

    @Override
    public SendEmailResponse sendRequest(Map<String, String> parameters) throws IOException {
	parameters.put(PARAM_TYPE, PARAM_TYPE_DEFAULT_VALUE);
	return super.sendRequest(parameters);
    }

    @Override
    public SendEmailResponse mapJson(String jsonString) {
	SendEmailResponse sendEmailResponse = new SendEmailResponse();
	try {
	    JSONObject serviceObj = parseResultAndError(sendEmailResponse, jsonString, SendEmailResponse.class.getSimpleName());
	    if (serviceObj == null) {
		return sendEmailResponse;
	    }
	} catch (Exception e) {
	    throw new IllegalStateException("Error processing sendEmailResponse response:", e);
	}
	return sendEmailResponse;
    }
}