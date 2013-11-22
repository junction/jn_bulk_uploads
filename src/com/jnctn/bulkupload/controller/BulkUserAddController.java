/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jnctn.bulkupload.controller;

import java.io.*;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;

import com.jnctn.bulkupload.model.*;
import com.jnctn.bulkupload.model.json.*;
import com.jnctn.bulkupload.service.ws.*;
import com.jnctn.bulkupload.util.LogFactory;

/**
 * Controller class that delegates to the service layer for interacting with the
 * Jnctn webservices. UI classes should make method calls directly to this class.
 *
 * @author Martin Constantine
 */
public class BulkUserAddController {

    public static final int PROCESSING_UNKNOWN = 0;
    public static final int PROCESSING_PHONES = 1;
    public static final int PROCESSING_USERS = 2;
    public static final int PROCESSING_EXTERNAL_RESOURCES = 3;

    private static final Logger logger = Logger.getLogger(BulkUserAddController.class);
    private final ProgressController progressController;
    private String sessionId                 = null;
    private String adminUsername             = null;
    private String adminPassword             = null;
    private String adminDomain               = null;
    private Long organizationId              = null;

    /** enhanced logging **/
    private long _countAnticipatedTotalUsers = 0;
    private long _countAnticipatedTotalVms   = 0;
    private long _countUserModAdded          = 0;
    private long _countUserModAlias          = 0;
    private long _countVmModAdded            = 0;
    private long _countVmModLink             = 0;

    private ArrayList<User> _users           = null;
    private ArrayList<Phone> _phones         = null;
    private ArrayList<ExternalResource> _externalResources = null;

    private int _isProcessingResource    = BulkUserAddController.PROCESSING_UNKNOWN;

    public BulkUserAddController(ProgressController progressController, String adminUsername,
				 String adminPassword, String adminDomain, boolean createSession) {
        this.adminUsername = adminUsername;
	this.adminPassword = adminPassword;
	this.adminDomain = adminDomain;
	this.progressController = progressController;
	this._users = new ArrayList<User>(20);
	this._phones = new ArrayList<Phone>(20);
        this._externalResources = new ArrayList<ExternalResource>(20);

	if (createSession) {
	    initSession();
	}
    }

    /**
     * Initializes the session by fetching a new session id
     * and the current organization ID.
     */
    public final void initSession() {
        this.sessionId = createSession(adminUsername, adminPassword, adminDomain);
	this.organizationId = fetchOrganizationId(adminDomain);
	this.resetMetrics();
    }

    private void resetMetrics(){
        _countAnticipatedTotalUsers = 0;
	_countAnticipatedTotalVms   = 0;
	_countUserModAdded          = 0;
	_countUserModAlias          = 0;
	_countVmModAdded            = 0;
	_countVmModLink             = 0;
	_isProcessingResource       = BulkUserAddController.PROCESSING_UNKNOWN;
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

    /** enhanced logging **/
    @Override
    public String toString() {
	String debug = "";
        String s     = "";
	s += "<html><table>";
	s += "<tr><td colspan='2'>Results Diagnostics</td></tr>";
	s += "<tr><td colspan='2'>====================</td></tr>";
	if (this._isProcessingResource == BulkUserAddController.PROCESSING_PHONES) {
	    s += "<tr><td>Total Phones Processed</td><td>" + this._countUserModAdded          + " / ";
	    s +=                                             this._countAnticipatedTotalUsers + "</td></tr>";
	} else if (this._isProcessingResource == BulkUserAddController.PROCESSING_USERS) {
	    s += "<tr><td>Total Users Processed</td><td>"  + this._countUserModAdded          + " / ";
	    s +=                                             this._countAnticipatedTotalUsers + "</td></tr>";
	    s += "<tr><td>Added voicemail </td><td>"       + this._countVmModAdded            + " / ";
	    s +=                                             this._countAnticipatedTotalVms   + "</td></tr>";
	    s += "<tr><td>Added alias </td><td>"           + this._countUserModAlias          + "</td></tr>";
	    s += "<tr><td>Added Voicemail Link </td><td>"  + this._countVmModLink 	      + "</td></tr>";
	} else if (this._isProcessingResource == BulkUserAddController.PROCESSING_EXTERNAL_RESOURCES) {
	    s += "<tr><td>Total External Addresses Processed</td><td>" + this._countUserModAdded          + " / ";
	    s += this._countAnticipatedTotalUsers + "</td></tr>";
        }
	s += "<table></html>";

	debug += "Results Diagnostics\n";
	debug += "====================\n";
	if (this._isProcessingResource == BulkUserAddController.PROCESSING_PHONES) {
	    debug += "Total Phones Processed " + this._countUserModAdded          + " / ";
	    debug +=                             this._countAnticipatedTotalUsers + "\n";
        } else if (this._isProcessingResource == BulkUserAddController.PROCESSING_USERS) {
	    debug += "Total Users Processed "  + this._countUserModAdded          + " / ";
	    debug +=                             this._countAnticipatedTotalUsers + "\n";
	    debug += "Added voicemail "        + this._countVmModAdded            + " / ";
	    debug +=                             this._countAnticipatedTotalVms   + "\n";
	    debug += "Added alias "            + this._countUserModAlias          + "\n";
	    debug += "Added Voicemail Link "   + this._countVmModLink             + "\n";
	} else if (this._isProcessingResource == BulkUserAddController.PROCESSING_EXTERNAL_RESOURCES) {
	    debug += "Total External Addresses Processed " + this._countUserModAdded          + " / ";
	    debug +=  this._countAnticipatedTotalUsers + "\n";
        }

        logger.info(debug);
        return s;
    }

    public ArrayList<User> getUsersList  () {
	return this._users;
    }

    public ArrayList<Phone> getPhonesList() {
	return this._phones;
    }

    public ArrayList<ExternalResource> getExternalResourceList  () {
	return this._externalResources;
    }

    private int getResource2Process(String[] tokens) {
        int resource2Process = BulkUserAddController.PROCESSING_UNKNOWN;
        if (tokens.length >= 1) {
            if (tokens[0].equalsIgnoreCase("first")) {
                resource2Process = BulkUserAddController.PROCESSING_USERS;
            } else if (tokens[0].toLowerCase().startsWith("external")) {
                resource2Process = BulkUserAddController.PROCESSING_EXTERNAL_RESOURCES;
            } else {
                resource2Process = BulkUserAddController.PROCESSING_PHONES;
            }
        } else {
            logger.error("Not sure what we're uploading (Phones, Users, or External SIP or Telephone numbers");
        }
        return resource2Process;
    }

    public void parseCsv(File csvFile) throws FileNotFoundException, IOException {
        this.resetMetrics();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(csvFile)));

	this._users = new ArrayList<User>(20);
	this._phones = new ArrayList<Phone>(20);
        this._externalResources = new ArrayList<ExternalResource>(20);

	String line = null, f;
	StrTokenizer tokenizer  = StrTokenizer.getCSVInstance();
	tokenizer.setIgnoreEmptyTokens(false);

	line = reader.readLine();
	tokenizer.reset(line);
	String[] tokens = tokenizer.getTokenArray();
	logger.info ("Number of TOKENS Found in header : " + tokens.length + " line " + line);
        this._isProcessingResource = this.getResource2Process(tokens);

	while ((line = reader.readLine()) != null) {
	    tokenizer.reset(line);
            tokens = tokenizer.getTokenArray();
            if (line.length() > 0) {
                this._countAnticipatedTotalUsers++;
                if (this._isProcessingResource == BulkUserAddController.PROCESSING_USERS) {
                    this._countAnticipatedTotalVms = "y".equalsIgnoreCase(tokens[4]) ?
		        this._countAnticipatedTotalVms + 1: this._countAnticipatedTotalVms;
                }
            }
	 }
	 reader.close();

	 reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));
	 line = reader.readLine();
	 while ((line = reader.readLine()) != null) {
	     tokenizer.reset(line);
	     if (line.length() > 0) {
                 tokens = tokenizer.getTokenArray();
                 if (this._isProcessingResource == BulkUserAddController.PROCESSING_PHONES) {
                     this._phones.add(constructPhonesFromLineParts(tokens));
                 } else if(this._isProcessingResource == BulkUserAddController.PROCESSING_USERS) {
                     this._users.add(constructUserFromLineParts(tokens));
                 } else if(this._isProcessingResource == BulkUserAddController.PROCESSING_EXTERNAL_RESOURCES) {
                     this._externalResources.add(constructExternalFromLineParts(tokens));
                 }
	     }
	 }
	 reader.close();
     }

     private ExternalResource constructExternalFromLineParts(String[] tokens) {
         ExternalResource resource = null;
         String r = tokens[0].trim();
         if (r.indexOf("@") != -1) {
             resource = new ExternalAddress(tokens[1].trim(), r);
         } else {
             resource = new TelephoneNumberAddress(r);
         }
         return resource;
     }

    /**
     * Builds a phone object from the given array.
     *
     * <Parameter>Make</Parameter>
     * <Parameter>Model</Parameter>
     * <Parameter>MacAddress</Parameter>
     * <Parameter>GmtOffset</Parameter>
     * <Parameter>NatKeepalive</Parameter>
     * <Parameter>OrganizationWebPassword</Parameter>
     * @return
     */
     private Phone constructPhonesFromLineParts(String[] tokens) {
         boolean valid = false;
	 Phone   phone = new Phone();
	 phone.setMacAddress (tokens[0].trim());
	 phone.setMake       (tokens[1].trim());
	 phone.setModel      (tokens[2].trim());
	 phone.setGmtOffset  (tokens[3].trim());
         phone.setNatKeepalive(tokens.length >= 5 && tokens[4].trim().equalsIgnoreCase("Y"));
         phone.setOrganizationWebPassword(tokens.length == 6 && tokens[5].trim().equalsIgnoreCase("Y"));

	 if (StringUtils.isEmpty(phone.getMacAddress())) {
	     /** TODO **/
	     /** Add a bit more validation to Mac address **/
	     throw new IllegalArgumentException("Phone Mac address is invalid");
	 }
	 valid = false;
	 if (StringUtils.isEmpty(phone.getMake())) {
	     throw new IllegalArgumentException("Phone Make was not specified");
	 } else {
	     for (int i = 0; i < Phone.MAKES.length; i++) {
		 if (Phone.MAKES[i].equalsIgnoreCase (phone.getMake())) {
		     valid = true;
		     break;
		 }
	     }
	     if (!valid) {
		 throw new IllegalArgumentException("Phone MAKE was invalid for MacAddress " + phone.getMacAddress());
	     }
	 }
	 valid = false;
	 if (StringUtils.isEmpty(phone.getModel())) {
	     throw new IllegalArgumentException("Phone Model was not specified");
	 }

	 valid = false;
	 if (StringUtils.isEmpty(phone.getGmtOffset())) {
	     throw new IllegalArgumentException("Gmt offset was not specified");
	 } else {
	     for (int i = 0; i < Phone.GMT_OFFSET_ALIAS.length; i++) {
                 if (Phone.GMT_OFFSET_ALIAS[i].equalsIgnoreCase (phone.getGmtOffset())) {
                     phone.setGmtOffset(Phone.GMT_OFFSET[i]);
                     valid = true;
                     break;
                 }
	     }
	     if (!valid) {
	    	 throw new IllegalArgumentException("GMT OFFSET was invalid for MacAddress " + phone.getMacAddress());
	     }
	 }
	 return phone;
     }

    /** Builds a user object from the given array **/
     private User constructUserFromLineParts(String[] tokens) {
         User user = new User();
	 user.setFirstName(tokens[0].trim());
	 user.setLastName(tokens[1].trim());
	 if (StringUtils.isEmpty(user.getFirstName()) && StringUtils.isEmpty(user.getLastName())) {
	     throw new IllegalArgumentException("Neither first name nor last name are available.");
	 }

	 try {
	     user.setExtension(new Integer(tokens[2]));
	 } catch (NumberFormatException nfe) {
	     throw new IllegalArgumentException("Extension must be a valid integer: " + tokens[2]);
	 }

	 if (!tokens[3].matches(".+@.+\\.[a-z]+")) {
	     throw new IllegalArgumentException("Email address is invalid: "          + tokens[3]);
	 }

	 user.setEmail(tokens[3].trim());
	 user.setAddVoicemail("y".equalsIgnoreCase(tokens[4].trim()));
         user.setShouldSendEmail(tokens.length >= 6 && "y".equalsIgnoreCase(tokens[5].trim()));
	 return user;
     }

     /** Uploads a collection of users to the jctn service **/
    public void bulkUpload(ArrayList<User> users) {
	progressController.setIndeterminate(false);
	int i = 1;
	progressController.setCurrentTask("Adding users...", 0, 100);
	for (User user : users) {
	    logger.info("Uploading user: " + user);
	    try {
		progressController.setProgressLabelText("Processing user: " + user.getEmail());
		uploadSingleUser(user);
		progressController.setProgress(i++ * 100 / users.size());
	    } catch (Exception e) {
		logger.error("Error uploading user: " + e.toString(), e);
	    } finally {
		logUser(user);
	    }
	}
	progressController.done();
    }


     /**Uploads a collection of phones to the 'jctn' service **/
    public void bulkUpload(Collection<Phone> phones) {
	progressController.setIndeterminate(false);
	int i = 1;
	progressController.setCurrentTask("Adding phones ...", 0, 100);
	for (Phone phone : phones) {
	    logger.info("Uploading phone: " + phone);
	    try {
		progressController.setProgressLabelText("Processing phone: " + phone.getMacAddress());
		uploadPhone(phone);
		progressController.setProgress(i++ * 100 / phones.size());
	    } catch (Exception e) {
		logger.error("Error uploading phone: " + e.toString(), e);
	    } finally {
		logPhone (phone);
	    }
	}
	progressController.done();
    }


    public void bulkUpload1(Collection<ExternalResource> externalResources) {
	progressController.setIndeterminate(false);
	int i = 1;
	progressController.setCurrentTask("Adding external resource ...", 0, 100);
	for (ExternalResource resource : externalResources) {
	    logger.info("Uploading resource: " + resource);
	    try {
		progressController.setProgressLabelText("Processing resource: " + resource.getUsername());
		uploadResource(resource);
		progressController.setProgress(i++ * 100 / externalResources.size());
	    } catch (Exception e) {
		logger.error("Error uploading resource: " + e.toString(), e);
	    } finally {
		logResource(resource);
	    }
	}
	progressController.done();
    }

    private void uploadResource(ExternalResource resource) throws IOException {
	/** Add resource **/
	logger.info("Step 1: Adding new resource " + resource.getUsername());
        if (resource instanceof  TelephoneNumberAddress) {
            execExternalTelephoneNumberAdd(resource, new TelephoneNumberAddressAdd());
        } else {
            execExternalAddressAdd(resource, new ExternalAddressAdd());
        }
	if (!StringUtils.isEmpty(resource.getError())) {
	    return;
	} else {
	    this._countUserModAdded++;
	}
    }

    private void uploadPhone (Phone phone) throws IOException {
	/** Add phone **/
	logger.info("Step 1: Adding new phone with MacAddress " + phone.getMacAddress());
	execPhoneAdd(phone, new PhoneAdd());
	if (!StringUtils.isEmpty(phone.getError())) {
	    return;
	} else {
	    this._countUserModAdded++;
	}
    }

    private void uploadSingleUser(User user) throws IOException {

        /** add user--UserAdd **/
        logger.info("Adding new user: " + user.getEmail());
        logger.debug("Step 1: UserAdd");
        String originalAuthUsername = null;
        String originalUsername     = null;
        boolean usenamesGood        = false;
        int suffix                  = 1;
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

        /** add extension--UserAliasAdd **/
        logger.debug("Step 2: UserAliasAdd (extension)");
        execUserAliasAdd(user, new UserAliasAdd());
        if (!StringUtils.isEmpty(user.getError())) {
            return;
        } else{
            this._countUserModAlias++;
        }

        /** If the user doesn't want voice mail, we're done **/
        if (!user.isAddVoicemail()) {
            logger.debug("User does not want voicemail. All done.");
            return;
        }

        /** add vm box--VoicemailoboxAdd **/
        logger.debug("Step 3: VoicemailBoxAdd");
        execVoicemailBoxAdd(user, new VoicemailboxAdd());

        if (!StringUtils.isEmpty(user.getError())) {
            return;
        } else {
            this._countVmModAdded++;
        }

        /** link user to vm--UserAddressEdit **/
        logger.debug("Step 4: UserAddressEdit (link voicemail)");
        execUserAddressEdit(user, new UserAddressEdit());
        if (StringUtils.isEmpty(user.getError())) {
            this._countVmModLink++;
        }

        if (!user.getShouldSendEmail() &&
            !StringUtils.isEmpty(user.getError())) {
            return;
        }

        /** send welcome e-mail  **/
        logger.debug("Step 5: Send Welcome Email");
        execSendWelcomeEmail(user, new SendEmail());
        if (StringUtils.isEmpty(user.getError())) {
            // was there an error
        }
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
	    Level lOrig       = logContent.getLevel();
	    logContent.setLevel(Level.ERROR);
	    logHeader .setLevel(Level.ERROR);
	    SessionCreateResponse response = sessionCreate.sendRequest(params);
	    logContent.setLevel(lOrig);
	    logHeader .setLevel(lOrig);
	    /** validate the response **/
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

    private <T extends AbstractJSONResponse> boolean validateResponse(T response) {
        if (response == null) {
	    handleError("Null response", new IllegalStateException("Response object is null"));
	    return false;
	}

	if (response.hasError()) {
	    /** We know we have an error here so we'll just let the user know what it was **/
	    String errorStr = constructErrorString(response.getErrors());
	    handleError("Error in response: " + errorStr, new IllegalStateException("Response error"));
	    return false;
	}
	return true;
    }

    private void handleError(String string, Exception e) {
        logger.error(string, e);
    }

    private Long fetchOrganizationId(String adminDomain) {
        Map<String, String> params = new HashMap<String, String>();
	params.put(UserAdd.PARAM_SESSION_ID, getSessionId());
	/** get the organization ID via OrgRead **/
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

    private String constructErrorString(Map<String, String> errorMap) {
        String errorStr = "";
	for (String errorKey : errorMap.keySet()) {
	    errorStr += errorKey + "=>" + errorMap.get(errorKey) + ",";
	}

	if (errorStr.endsWith(",")) {
	    errorStr = errorStr.substring(0, errorStr.length() - 1);
	}
	return errorStr;
    }

    /**
     * @param user
     * @throws IOException
     */
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
	     logger.debug("Added phone ");
	 } else {
	     logger.debug("Failed to add phone");
	     phone.setError(constructErrorString(response.getErrors()));
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

    void execExternalTelephoneNumberAdd(ExternalResource resource, TelephoneNumberAddressAdd telephoneNumberAddressAdd) throws IOException {
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

    void execExternalAddressAdd(ExternalResource resource, ExternalAddressAdd externalAddressAdd) throws IOException {
        ExternalAddress externalAddress = (ExternalAddress) resource;
        Map<String, String> params = new HashMap<String, String>();
	params.put(ExternalAddressAdd.PARAM_SESSION_ID, getSessionId());
	params.put(ExternalAddressAdd.PARAM_DOMAIN, this.adminDomain);
	params.put(ExternalAddressAdd.PARAM_ORGANIZATION_ID, getOrganizationId() + "");
	params.put(ExternalAddressAdd.PARAM_USERNAME, resource.getUsername());
	params.put(ExternalAddressAdd.PARAM_FOREIGN_ADDRESS, externalAddress.getForeignAddress());

	ExternalAddressAddResponse response = externalAddressAdd.sendRequest(params);
	if (validateResponse(response)) {
	    logger.debug("Sent External Resource Add");
	} else {
	    logger.debug("Failed to send Resource Add");
	    resource.setError(constructErrorString(response.getErrors()));
	}
    }

    private void logUser(User user) {
        logger.info(user);
    }

    private void logPhone(Phone phone) {
	logger.info(phone);
    }

    private void logResource(ExternalResource resource) {
        logger.info(resource);
    }

}
