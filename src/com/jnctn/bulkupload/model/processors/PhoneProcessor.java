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

public class PhoneProcessor extends BaseProcessor {

    private static final Logger logger = Logger.getLogger(PhoneProcessor.class);

    protected long countPhonesAdded = 0;

    public PhoneProcessor(ProgressController progress, String username,
        String password, String domain, boolean createSession) {

	this.progressController = progress;
        this.adminUsername = username;
	this.adminPassword = password;
	this.adminDomain = domain;

	if (createSession) {
	    initSession();
	}
    }

    protected void resetMetrics() {
        this.countPhonesAdded = 0;
    }

    @Override
    public String toString() {
	StringBuffer debug = new StringBuffer();
        StringBuffer stats = new StringBuffer();

	stats.append("<html><table>");
	stats.append("<tr><td colspan='2'>Results Diagnostics</td></tr>");
	stats.append("<tr><td colspan='2'>====================</td></tr>");
        stats.append("<tr><td>Total Phones Processed</td><td>");
        stats.append(this.countPhonesAdded + " of " + resources.size());
        stats.append("</td></tr>");
	stats.append("<table></html>");

	debug.append("Results Diagnostics\n");
	debug.append("============================================================\n");
        debug.append("Total Phones Processed ");
        debug.append(this.countPhonesAdded + " of " + resources.size() + "\n");

        logger.info(debug.toString());

        printPostProcessErrors();

        return stats.toString();
    }

    public void upload() {
        int i = 1, num = resources.size();
	this.progressController.setIndeterminate(false);
	this.progressController.setCurrentTask("Adding phones ...", 0, 100);
	for (IUploadable resource : resources) {
            Phone phone = (Phone) resource;
	    logger.info("Uploading phone: " + phone);
	    try {
		this.progressController.setProgressLabelText("Processing phone: " + phone.getMacAddress());
		upload(phone);
		this.progressController.setProgress(i++ * 100 / num);
	    } catch (Exception e) {
		logger.error("Error uploading phone: " + e.toString(), e);
	    } finally {
		logPhone(phone);
	    }
	}
	this.progressController.done();
    }

    private void upload(Phone phone) throws IOException {
	logger.info("Step 1: Adding new phone with MacAddress " + phone.getMacAddress());
	execPhoneAdd(phone, new PhoneAdd());
	if (!StringUtils.isEmpty(phone.getError())) {
            errors.add("Failed to add phone " + phone.toString());
	    return;
	} else {
	    this.countPhonesAdded++;
	}
    }

    void execPhoneAdd(Phone phone, PhoneAdd phoneAdd) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(PhoneAdd.PARAM_SESSION_ID      , getSessionId());
        params.put(PhoneAdd.PARAM_ORGANIZATION_ID , getOrganizationId() + "");
        params.put(PhoneAdd.PARAM_GMTOFFSET       , phone.getGmtOffset());
        params.put(PhoneAdd.PARAM_MACADDRESS      , phone.getMacAddress());
        params.put(PhoneAdd.PARAM_MAKE            , phone.getMake());
        params.put(PhoneAdd.PARAM_MODEL           , phone.getModel());
        params.put(PhoneAdd.PARAM_NATKEEPALIVE    , phone.getNatKeepalive() + "");
        params.put(PhoneAdd.PARAM_ORGANIZATION_WEB_PASSWORD, phone.getOrganizationWebPassword() + "");
        PhoneAddResponse response = phoneAdd.sendRequest(params);
        if (validateResponse(response)) {
            logger.debug("Added phone");
        } else {
            logger.debug("Failed to add phone");
            phone.setError(constructErrorString(response.getErrors()));
        }
    }

    private void logPhone(Phone phone) {
	logger.info(phone);
    }

}