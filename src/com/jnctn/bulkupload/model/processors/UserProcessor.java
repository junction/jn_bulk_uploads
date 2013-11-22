package com.jnctn.bulkupload.model.processors;

import java.io.*;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;

import com.jnctn.bulkupload.model.*;
import com.jnctn.bulkupload.model.json.*;
import com.jnctn.bulkupload.service.ws.*;
import com.jnctn.bulkupload.controller.*;
import com.jnctn.bulkupload.util.LogFactory;

public class UserProcessor extends BaseProcessor {

    private static final Logger logger = Logger.getLogger(UserProcessor.class);

    protected long _countAnticipatedTotalUsers = 0;
    protected long _countAnticipatedTotalVms   = 0;
    protected long _countUserModAdded          = 0;
    protected long _countUserModAlias          = 0;
    protected long _countVmModAdded            = 0;
    protected long _countVmModLink             = 0;

    public UserProcessor(ProgressController progress, String username,
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
        _countAnticipatedTotalUsers = 0;
	_countAnticipatedTotalVms   = 0;
	_countUserModAdded          = 0;
	_countUserModAlias          = 0;
	_countVmModAdded            = 0;
	_countVmModLink             = 0;
    }

    private long expectedVMCount() {
        long count = 0;
	for (IUploadable resource : resources) {
            User user = (User) resource;
            if (user.isAddVoicemail()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
	StringBuffer debug = new StringBuffer();
        StringBuffer stats = new StringBuffer();

        long expectedVmCount = this.expectedVMCount();
	stats.append("<html><table>");
	stats.append("<tr><td colspan='2'>Results Diagnostics</td></tr>");
	stats.append("<tr><td colspan='2'>====================</td></tr>");
	stats.append("<tr><td>Total Users Processed</td><td>");
        stats.append(this._countUserModAdded + " of " + resources.size() + "</td></tr>");
	stats.append("<tr><td>Added voicemail </td><td>");
        stats.append(this._countVmModAdded  + " of " + expectedVmCount + "</td></tr>");
        stats.append("<tr><td>Added alias </td><td>" + this._countUserModAlias + "</td></tr>");
	stats.append("<tr><td>Added Voicemail Link </td><td>" + this._countVmModLink + "</td></tr>");
	stats.append("<table></html>");

	debug.append("Results Diagnostics\n");
	debug.append("====================\n");
	debug.append("Total Users Processed ");
        debug.append(this._countUserModAdded + " of " + resources.size() + "\n");
        debug.append("Added voicemail " + this._countVmModAdded + " of " + expectedVmCount + "\n");
        debug.append("Added alias " + this._countUserModAlias + "\n");
        debug.append("Added Voicemail Link " + this._countVmModLink + "\n");

        logger.info(debug.toString());

        return stats.toString();
    }

    public void upload() {
        int i = 1, num = resources.size();
	this.progressController.setIndeterminate(false);
	progressController.setCurrentTask("Adding users...", 0, 100);
	for (IUploadable resource : resources) {
            User user = (User) resource;
	    logger.info("Uploading user: " + user);
	    try {
		this.progressController.setProgressLabelText("Processing user: " + user.getEmail());
		upload(user);
		this.progressController.setProgress(i++ * 100 / num);
	    } catch (Exception e) {
		logger.error("Error uploading user: " + e.toString(), e);
	    } finally {
		logUser(user);
	    }
	}
	this.progressController.done();
    }

    private void upload(User user) throws IOException {
        logger.info("Adding new user: " + user.getEmail());
        logger.debug("Step 1: UserAdd");
        String originalAuthUsername = null;
        String originalUsername = null;
        boolean usenamesGood = false;
        int suffix = 1;
        while (!usenamesGood) {
            execUserAdd(user);
            if (originalAuthUsername == null) {
                originalAuthUsername = user.getAuthUsername();
            }
            if (originalUsername == null) {
                originalUsername = user.getUsername();
            }

            if (StringUtils.isEmpty(user.getError())) {
                break;
            }

            /**
             * @TODO:
             * see what kind of error message is returned for existing username and auth_username
             * and leverage here
             */
            if (user.getError().contains("username") || user.getError().contains("auth_username")) {
                String suffixStr = suffix + "";
                user.setUsername(originalUsername);
                user.setAuthUsername(originalAuthUsername);
                {
                    String currentAuthName = user.getAuthUsername();
                    if ((suffixStr + currentAuthName).length() > 32) {
                        currentAuthName = currentAuthName.substring(0, (32 - suffixStr.length()));
                    }
                    user.setAuthUsername(currentAuthName + suffixStr);
                }
                {
                    String currentUsername = user.getUsername();
                    if ((suffixStr + currentUsername).length() > 32) {
                        currentUsername = currentUsername.substring(0, (32 - suffixStr.length()));
                    }
                    user.setUsername(currentUsername + suffixStr);
                }
                suffix++;
                user.setError(null);
            } else {
                usenamesGood = true;
            }
        }

        if (!StringUtils.isEmpty(user.getError())) {
            return;
        } else{
            this._countUserModAdded++;
        }

        // add extension--UserAliasAdd
        logger.debug("Step 2: UserAliasAdd (extension)");
        execUserAliasAdd(user, new UserAliasAdd());
        if (!StringUtils.isEmpty(user.getError())) {
            return;
        } else{
            this._countUserModAlias++;
        }

        // If the user doesn't want voice mail, we're done
        if (!user.isAddVoicemail()) {
            logger.debug("User does not want voicemail. All done.");
            return;
        }

        // add vm box--VoicemailoboxAdd
        logger.debug("Step 3: VoicemailBoxAdd");
        execVoicemailBoxAdd(user, new VoicemailboxAdd());

        if (!StringUtils.isEmpty(user.getError())) {
            return;
        } else {
            this._countVmModAdded++;
        }

        // link user to vm--UserAddressEdit
        logger.debug("Step 4: UserAddressEdit (link voicemail)");
        execUserAddressEdit(user, new UserAddressEdit());
        if (StringUtils.isEmpty(user.getError())) {
            this._countVmModLink++;
        }

        if (!user.getShouldSendEmail() &&
            !StringUtils.isEmpty(user.getError())) {
            return;
        }

        // send welcome e-mail
        logger.debug("Step 5: Send Welcome Email");
        execSendWelcomeEmail(user, new SendEmail());
        if (StringUtils.isEmpty(user.getError())) {
            // was there an error
        }
    }

     private void execUserAdd(User user) throws IOException {
         Map<String, String> params = new HashMap<String, String>();
	 params.put(UserAdd.PARAM_SESSION_ID, getSessionId());
	 UserAdd userAdd = new UserAdd();
	 /** Username and authusername **/
	 if (user.getAuthUsername() == null) {
	     user.setAuthUsername(userAdd.createAuthUsername(user.getEmail(), adminDomain));
	 }
	 if (user.getUsername() == null) {
	     user.setUsername(userAdd.createUsername(user.getEmail().split("@")[0]));
	 }
	 params.put(UserAdd.PARAM_ORGANIZATION_ID , getOrganizationId() + "");
	 params.put(UserAdd.PARAM_USERNAME        , user.getUsername());
	 params.put(UserAdd.PARAM_AUTH_USERNAME   , user.getAuthUsername());
	 params.put(UserAdd.PARAM_PASSWORD        , user.getPassword());
	 params.put(UserAdd.PARAM_PASSWORD_CONFIRM, user.getPassword());
	 params.put(UserAdd.PARAM_EMAIL           , user.getEmail());
	 params.put(UserAdd.PARAM_DOMAIN          , this.adminDomain);
	 params.put(UserAdd.PARAM_NAME            , user.getFullName());
	 UserAddResponse response = userAdd.sendRequest(params);
	 if (validateResponse(response)) {
	   user.setUserAdded(true);
	   user.setUserId(response.getUserId());
	 } else {
	   user.setUserAdded(false);
	   user.setError(constructErrorString(response.getErrors()));
	 }
     }

     /**
      * @param user
      */
     void execUserAliasAdd(User user, UserAliasAdd userAliasAdd) throws IOException {
         Map<String, String> params = new HashMap<String, String>();
	 params.put(UserAliasAdd.PARAM_SESSION_ID      , getSessionId());
	 params.put(UserAliasAdd.PARAM_ORGANIZATION_ID , getOrganizationId() + "");
	 params.put(UserAliasAdd.PARAM_ALIAS_USERNAME  , user.getExtension() + "");
	 params.put(UserAliasAdd.PARAM_ADDRESS_USERNAME, user.getUsername());
	 UserAliasAddResponse response = userAliasAdd.sendRequest(params);
	 if (validateResponse(response)) {
	     logger.debug("Adding user alias: " + response.getExtension());
	     user.setExtension(Integer.parseInt(response.getExtension()));
	 } else {
	     logger.debug("Failed to retrieve user alias.");
	     user.setExtensionAdded(false);
	     user.setError(constructErrorString(response.getErrors()));
	 }
     }

     /**
      * Adds a voicemail box to the current organization. The mailbox is intended
      * to be linked to the given user. This method does NOT link the mail box.
      * That must be done in a separate operation via {@linkplain UserAddressEdit}
      * @param user
      * @param voicemailboxAdd
      * @throws IOException
      */
     void execVoicemailBoxAdd(User user, VoicemailboxAdd voicemailboxAdd) throws IOException {
         Map<String, String> params = new HashMap<String, String>();
	 params.put(VoicemailboxAdd.PARAM_SESSION_ID     , getSessionId());
	 params.put(VoicemailboxAdd.PARAM_USERNAME       , user.getVmUsername());
	 params.put(VoicemailboxAdd.PARAM_FULLNAME       , user.getFullName());
	 params.put(VoicemailboxAdd.PARAM_MAILBOX        , user.getExtension() + "");
	 params.put(VoicemailboxAdd.PARAM_VMBOX_ID       , user.getExtension() + "");
	 params.put(VoicemailboxAdd.PARAM_DOMAIN         , adminDomain);
	 params.put(VoicemailboxAdd.PARAM_ORGANIZATION_ID, getOrganizationId() + "");
	 VoicemailboxAddResponse response = voicemailboxAdd.sendRequest(params);
	 if (validateResponse(response)) {
	     logger.debug("Mailbox added: " + response.getVmBoxId());
	     user.setVmMailBoxId(response.getVmBoxId());
	     user.setVmPassword(response.getPassword());
	     user.setVmBoxAdded(true);
	 } else {
	     logger.debug("Failed to add voicemail box.");
	     user.setVmBoxAdded(false);
	     user.setError(constructErrorString(response.getErrors()));
	 }
    }

    void execUserAddressEdit(User user, UserAddressEdit userAddressEdit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
	params.put(UserAddressEdit.PARAM_SESSION_ID     , getSessionId());
	params.put(UserAddressEdit.PARAM_ADDRESS        , user.getUsername()   + "@" + this.adminDomain);
	params.put(UserAddressEdit.PARAM_DEFAULT_ADDRESS, user.getVmUsername() + "@" + this.adminDomain);
	params.put(UserAddressEdit.PARAM_USERNAME       , user.getUsername());
	params.put(UserAddressEdit.PARAM_USER_ID        , user.getUserId() + "");
	UserAddressEditResponse response = userAddressEdit.sendRequest(params);
	if (validateResponse(response)) {
	    logger.debug("Mailbox successfully linked.");
	    user.setVmBoxLinked(true);
	} else {
	    logger.debug("Failed to link voicemail box.");
	    user.setVmBoxLinked(false);
	    user.setError(constructErrorString(response.getErrors()));
	}
    }

    void execSendWelcomeEmail(User user, SendEmail sendEmail) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
	params.put(SendEmail.PARAM_SESSION_ID, getSessionId());
	params.put(SendEmail.PARAM_USERNAME, user.getUsername()   + "@" + this.adminDomain);
	SendEmailResponse response = sendEmail.sendRequest(params);
	if (validateResponse(response)) {
	    logger.debug("Send welcome email successfully sent");
	} else {
	    logger.debug("Failed welcome email successfully sent");
	    user.setError(constructErrorString(response.getErrors()));
	}
    }

    private void logUser(User user) {
        logger.info(user);
    }

}
