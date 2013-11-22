package com.jnctn.bulkupload.service.ws;

import com.jnctn.bulkupload.model.json.OrganizationReadResponse;
import org.json.simple.JSONObject;

/**
 * @author martin
 */
public class OrganizationRead extends AbstractWebservice<OrganizationReadResponse> {
    public static final String PARAM_DOMAIN = "Domain";

    public OrganizationRead() {
	super.action = "OrganizationRead";
    }

    @Override
    public OrganizationReadResponse mapJson(String jsonString) {
	OrganizationReadResponse result = new OrganizationReadResponse();

	try {
	    JSONObject jOrgRead = parseResultAndError(result, jsonString, "OrganizationRead");

	    if(jOrgRead == null){
		return result;
	    }
			
	    JSONObject jOrg = (JSONObject) jOrgRead.get ("Organization");
	    Long      orgId = Long.parseLong ((String) jOrg.get("OrganizationId"));
	    result.setOrgId(orgId);

	} catch (Exception e) {
	    throw new IllegalStateException("Error processing response:", e);
	}

	return result;
    }
}
