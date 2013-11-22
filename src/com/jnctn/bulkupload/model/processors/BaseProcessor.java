package com.jnctn.bulkupload.model.processors;

import java.io.*;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;

import com.jnctn.bulkupload.controller.*;
import com.jnctn.bulkupload.model.*;
import com.jnctn.bulkupload.model.json.*;
import com.jnctn.bulkupload.service.ws.*;
import com.jnctn.bulkupload.util.LogFactory;

public abstract class BaseProcessor {

    private static final Logger logger = Logger.getLogger(BaseProcessor.class);

    protected ProgressController progressController = null;

    protected String adminUsername = null;
    protected String adminPassword = null;
    protected String adminDomain = null;
    protected String sessionId = null;
    protected Long organizationId = null;

    protected ArrayList<IUploadable> resources = new ArrayList<IUploadable>(20);

    public void add(IUploadable resource) {
        resources.add(resource);
    }

    public abstract void upload();

    protected abstract void resetMetrics();

    /**
     * Initializes the session by fetching a new session id
     * and the current organization ID.
     */
    public void initSession() {
        this.sessionId = createSession(adminUsername, adminPassword, adminDomain);
	this.organizationId = fetchOrganizationId(adminDomain);
	this.resetMetrics();
    }

    protected final String createSession(String adminUsername, String adminPassword, String adminDomain) {
        SessionCreate sessionCreate = new SessionCreate();
	Map<String, String> params  = new HashMap<String, String>();
	params.put("Username", this.adminUsername);
	params.put("Password", this.adminPassword);
	String sessionId_ = null;
	try {
	    logger.info("Turning off logger for CreateSession call");
	    Logger logContent = Logger.getLogger("httpclient.wire.content");
	    Logger logHeader  = Logger.getLogger("httpclient.wire.header");
	    Level lOrig = logContent.getLevel();
	    logContent.setLevel(Level.ERROR);
	    logHeader.setLevel(Level.ERROR);
	    SessionCreateResponse response = sessionCreate.sendRequest(params);
	    logContent.setLevel(lOrig);
	    logHeader.setLevel(lOrig);
	    // validate the response
	    if (!validateResponse(response)) {
	        return sessionId_;
	    }
	    sessionId_ = response.getSessionId();
	    logger.info("CreateSession call returned successfully with ID " + sessionId_);
	} catch (Exception e) {
	    handleError("Error creating session.", e);
	    throw new IllegalStateException(e);
	}
	return sessionId_;
    }

    protected String constructErrorString(Map<String, String> errorMap) {
        String errorStr = "";
	for (String errorKey : errorMap.keySet()) {
	    errorStr += errorKey + "=>" + errorMap.get(errorKey) + ",";
	}

	if (errorStr.endsWith(",")) {
	    errorStr = errorStr.substring(0, errorStr.length() - 1);
	}
	return errorStr;
    }

    protected Long fetchOrganizationId(String adminDomain) {
        Map<String, String> params = new HashMap<String, String>();
	params.put(UserAdd.PARAM_SESSION_ID, getSessionId());

	// get the organization ID via OrgRead
	OrganizationRead orgRead = new OrganizationRead();
	OrganizationReadResponse orgReadResponse = null;
	params.put(OrganizationRead.PARAM_DOMAIN, adminDomain);

	try {
	    orgReadResponse = orgRead.sendRequest(params);
	    if (!validateResponse(orgReadResponse)) {
	        throw new IllegalStateException("Response validation failed");
	    }
	} catch (Exception e) {
	    handleError("Error reading organization", e);
	    throw new IllegalStateException(e);
	}

	return orgReadResponse.getOrgId();
    }

    protected <T extends AbstractJSONResponse> boolean validateResponse(T response) {
        if (response == null) {
	    handleError("Null response", new IllegalStateException("Response object is null"));
	    return false;
	}

	if (response.hasError()) {
	    // We know we have an error here so we'll just let the user know what it was
	    String errorStr = constructErrorString(response.getErrors());
	    handleError("Error in response: " + errorStr, new IllegalStateException("Response error"));
	    return false;
	}
	return true;
    }

    private void handleError(String string, Exception e) {
        logger.error(string, e);
    }

    public String getSessionId() {
        return sessionId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setAdminDomain(String adminDomain) {
        this.adminDomain = adminDomain;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

}