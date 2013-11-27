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

public class ExternalResourceProcessor extends BaseProcessor {

    private static final Logger logger = Logger.getLogger(ExternalResourceProcessor.class);

    private ArrayList<ExternalResource> _externalResources = null;

    private int resourcesAdded = 0;
    private int extensionsAdded = 0;

    public ExternalResourceProcessor(ProgressController progress, String username,
        String password, String domain, boolean createSession) {

	this.progressController = progress;
        this.adminUsername = username;
	this.adminPassword = password;
	this.adminDomain = domain;

	if (createSession) {
	    initSession();
	}
    }

    protected void resetMetrics(){
	this.resourcesAdded = 0;
        this.extensionsAdded = 0;
    }

    private long expectedExtensionCount() {
        long count = 0;
        for (IUploadable resource : resources) {
            if (resource instanceof ExternalAddress) {
                ExternalAddress address = (ExternalAddress) resource;
                if (address.getExtension() != null) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public String toString() {
	StringBuffer debug = new StringBuffer();
        StringBuffer stats = new StringBuffer();

	stats.append("<html><table>");
	stats.append("<tr><td colspan='2'>Results Diagnostics</td></tr>");
	stats.append("<tr><td colspan='2'>====================</td></tr>");
        stats.append("<tr><td>Total External Addresses Processed</td><td>");
        stats.append(this.resourcesAdded + " of " + resources.size());
        stats.append("</td></tr>");
        if (this.expectedExtensionCount() > 0) {
            stats.append("<tr><td>Total Extensions Processed</td><td>");
            stats.append(this.extensionsAdded + " of " + this.expectedExtensionCount());
            stats.append("</td></tr>");
        }
        stats.append("<table></html>");

        debug.append("Results Diagnostics\n");
	debug.append("==================================================\n");
        debug.append("Total External Addresses Processed ");
        debug.append(this.resourcesAdded + " of " + resources.size() + "\n");
        if (this.expectedExtensionCount() > 0) {
            debug.append("Total Extensions Processed ");
            debug.append(this.extensionsAdded + " of " + this.expectedExtensionCount() + "\n");
        }

        logger.info(debug);

        printPostProcessErrors();

        return stats.toString();
    }

    public void upload() {
        int i = 1, num = resources.size();
	this.progressController.setIndeterminate(false);
	this.progressController.setCurrentTask("Adding external resource ...", 0, 100);
	for (IUploadable resource : resources) {
            ExternalResource external = (ExternalResource) resource;
	    logger.info("Uploading resource: " + resource);
	    try {
		this.progressController.setProgressLabelText("Processing resource: " + external.getUsername());
		upload(external);
		this.progressController.setProgress(i++ * 100 / num);
	    } catch (Exception e) {
		logger.error("Error uploading resource: " + e.toString(), e);
	    } finally {
                logger.info(external);
	    }
	}
	this.progressController.done();
    }

    private void upload(ExternalResource resource) throws IOException {
        boolean hasExtension = false;
	logger.info("Step 1: Adding new resource " + resource.getUsername());
        if (resource instanceof TelephoneNumberAddress) {
            execExternalTelephoneNumberAdd(resource, new TelephoneNumberAddressAdd());
        } else {
            ExternalAddress externalAddress = (ExternalAddress) resource;
            hasExtension = (externalAddress.getExtension() != null);
            execExternalAddressAdd(resource, new ExternalAddressAdd());
        }

	if (!StringUtils.isEmpty(resource.getError())) {
            errors.add("Failed to add external resource " + resource.toString());
	    return;
	} else {
	    this.resourcesAdded++;
	}
        if (!hasExtension) return;
        logger.info("Step 2: Adding the extension");
        execAppExtensionAddExtension(resource, new AppExtensionAddExtension());
	if (!StringUtils.isEmpty(resource.getError())) {
            errors.add("Failed to add extension " + resource.toString());
	    return;
	} else {
            this.extensionsAdded++;
	}
    }

    private void execExternalTelephoneNumberAdd(ExternalResource resource,
        TelephoneNumberAddressAdd telephoneNumberAddressAdd) throws IOException {

        Map<String, String> params = new HashMap<String, String>();
	params.put(TelephoneNumberAddressAdd.PARAM_SESSION_ID, getSessionId());
	params.put(TelephoneNumberAddressAdd.PARAM_DOMAIN, this.adminDomain);
	params.put(TelephoneNumberAddressAdd.PARAM_ORGANIZATION_ID, getOrganizationId() + "");
	params.put(TelephoneNumberAddressAdd.PARAM_USERNAME, resource.getUsername());
	params.put(TelephoneNumberAddressAdd.PARAM_NAME, resource.getName());

	TelephoneNumberAddressAddResponse response = telephoneNumberAddressAdd.sendRequest(params);
	if (validateResponse(response)) {
	    logger.debug("Sent External Resource Add");
	} else {
	    logger.debug("Failed to send Resource Add");
	    resource.setError(constructErrorString(response.getErrors()));
	}
    }

    private void execExternalAddressAdd(ExternalResource resource,
        ExternalAddressAdd externalAddressAdd) throws IOException {

        ExternalAddress externalAddress = (ExternalAddress) resource;

        Map<String, String> params = new HashMap<String, String>();
	params.put(ExternalAddressAdd.PARAM_SESSION_ID, getSessionId());
	params.put(ExternalAddressAdd.PARAM_DOMAIN, this.adminDomain);
	params.put(ExternalAddressAdd.PARAM_ORGANIZATION_ID, getOrganizationId() + "");
	params.put(ExternalAddressAdd.PARAM_USERNAME, resource.getUsername());
	params.put(ExternalAddressAdd.PARAM_NAME, resource.getName());
	params.put(ExternalAddressAdd.PARAM_FOREIGN_ADDRESS, externalAddress.getForeignAddress());

	ExternalAddressAddResponse response = externalAddressAdd.sendRequest(params);
	if (validateResponse(response)) {
	    logger.debug("Sent External Resource Add");
	} else {
	    logger.debug("Failed to send Resource Add");
	    resource.setError(constructErrorString(response.getErrors()));
	}
    }

     /**
      *
      */
     void execAppExtensionAddExtension(ExternalResource resource,
         AppExtensionAddExtension appExtensionAddExtension) throws IOException {

        ExternalAddress externalAddress = (ExternalAddress) resource;

         Map<String, String> params = new HashMap<String, String>();

	 params.put(AppExtensionAddExtension.PARAM_SESSION_ID, getSessionId());
	 params.put(AppExtensionAddExtension.PARAM_ORGANIZATION_ID, getOrganizationId() + "");
	 params.put(AppExtensionAddExtension.PARAM_EXTENSION, externalAddress.getExtension() + "");
	 params.put(AppExtensionAddExtension.PARAM_ADDRESS_USERNAME, externalAddress.getUsername());

	 AppExtensionAddExtensionResponse response = appExtensionAddExtension.sendRequest(params);

	 if (validateResponse(response)) {
	     logger.debug("Adding extension ");
	 } else {
	     logger.debug("Failed to add extension.");
             resource.setError(constructErrorString(response.getErrors()));
	 }
     }
}