package com.jnctn.bulkupload.service.ws;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import org.json.simple.JSONObject;
import com.jnctn.bulkupload.model.json.PhoneAddResponse;

/**
 * Webservice programming interface for adding an extension to to a user's address.
 *
 * @author martin
 */
public class PhoneAdd extends AbstractWebservice<PhoneAddResponse> {
    //public static final  String PARAM_ALIAS_USERNAME = "AliasUsername";
    /** Parameter name for organization ID.**/
    public static final String PARAM_ORGANIZATION_ID = "OrganizationId";
    public static final String PARAM_MACADDRESS      = "MacAddress";
    public static final String PARAM_GMTOFFSET       = "GmtOffset";
    public static final String PARAM_MAKE            = "Make";
    public static final String PARAM_MODEL           = "Model";
    public static final String PARAM_NATKEEPALIVE    = "NatKeepalive";
    public static final String PARAM_ORGANIZATION_WEB_PASSWORD = "OrganizationWebPassword";

    public PhoneAdd() {
	super.action = "PhoneAdd";
    }

    @Override
    public PhoneAddResponse sendRequest(Map<String, String> parameters) throws IOException {
	return super.sendRequest(parameters);
    }

    @Override
    public PhoneAddResponse mapJson(String jsonString) {
	PhoneAddResponse phoneAddResponse = new PhoneAddResponse();
	try {
	    JSONObject serviceObj = parseResultAndError(phoneAddResponse, jsonString, PhoneAdd.class.getSimpleName());
	    if (serviceObj == null) {
		return phoneAddResponse;
	    }

	    //JSONObject jPhoneAlias =  (JSONObject) serviceObj.get("PhoneAlias");
	    //phoneAddResponse.setExtension((String) jPhoneAlias.get("AliasUsername"));
	} catch (Exception e) {
	    throw new IllegalStateException("Error processing PhoneAdd response:", e);
	}
	return phoneAddResponse;
    }

    public static void main (String[] args) {
      // System.out.println("This is a test for class Phone Add");
      PhoneAdd pa = new PhoneAdd();
      Map<String, String> params = new HashMap<String, String>();
      //params.put(PhoneAdd.PARAM_SESSION_ID, getSessionId());
      // params.put(PhoneAdd.PARAM_ORGANIZATION_ID, getOrganizationId() + "");
      //params.put(PhoneAdd.PARAM_ADDRESS_USERNAME, user.getUsername());
      try {
          PhoneAddResponse response = pa.sendRequest(params);
      /**
      if (validateResponse(response)) {
	user.setExtension(Integer.parseInt(response.getExtension()));
      } else {
	logger.debug("Failed to retrieve user alias.");
	user.setExtensionAdded(false);
	user.setError(constructErrorString(response.getErrors()));
      }
      **/
      } catch (IOException ioe) {
          System.out.println("This is an IOException " + ioe.toString());
      }
      System.out.println("Sent response");
    }
}
