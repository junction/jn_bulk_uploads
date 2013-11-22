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

public class CsvParser {

    private static final Logger logger = Logger.getLogger(CsvParser.class);

    public static final int USER_RESOURCE = 1;
    public static final int PHONE_RESOURCE = 2;
    public static final int EXTERNAL_RESOURCE = 3;
    public static final int UNKNOWN_RESOURCE = 5;

    private CsvResourceFactory factory = null;

    public CsvParser(File csvFile) {
        factory = new CsvResourceFactory();
        try {
            parseCsv(csvFile);
        } catch(Exception ex) {
            logger.error("Error parsing CSV file " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public BaseProcessor getProcessor(ProgressController progress,
        String user, String password, String domain) {

        BaseProcessor processor = null;

        int type = factory.getType();

        switch(type) {
            case CsvParser.PHONE_RESOURCE:
                 processor = new PhoneProcessor(progress, user, password, domain, true);
            break;
            case CsvParser.EXTERNAL_RESOURCE:
                 processor = new ExternalResourceProcessor(progress, user, password, domain, true);
            break;
            case CsvParser.USER_RESOURCE:
                processor = new UserProcessor(progress, user, password, domain, true);
            break;
            default:
                logger.error("Resource type is unknown, not sure if Phone, User or External resource");
            break;
        }

        ArrayList<IUploadable> resources = factory.getUploadableResources();

        for (IUploadable resource : resources) {
            processor.add(resource);
        }

        return processor;
    }

    public class CsvResourceFactory {

        private int type = CsvParser.UNKNOWN_RESOURCE;

        ArrayList<IUploadable> resources = null;

        public CsvResourceFactory() {
            resources = new ArrayList<IUploadable>(100);
        }

        public int getType() {
            return this.type;
        }

        public ArrayList<IUploadable> getUploadableResources() {
            return resources;
        }

        public void setResourceType(String [] tokens) {
            if (tokens.length >= 1) {
                if (tokens[0].toLowerCase().startsWith("first")) {
                    this.type = CsvParser.USER_RESOURCE;
                } else if (tokens[0].toLowerCase().startsWith("external")) {
                    this.type = CsvParser.EXTERNAL_RESOURCE;
                } else {
                    this.type = CsvParser.PHONE_RESOURCE;
                }
            } else {
                logger.error("Not sure what we're uploading (Phones, Users, or External SIP or Telephone numbers");
            }
        }

        public void parse(String[] tokens) {
            if (tokens.length >= 1 && this.type != CsvParser.UNKNOWN_RESOURCE) {
                if (this.type == CsvParser.USER_RESOURCE) {
                    User user = constructUserFromLineParts(tokens);
                    resources.add(user);
                } else if (this.type == CsvParser.EXTERNAL_RESOURCE) {
                    ExternalResource external = constructExternalFromLineParts(tokens);
                    resources.add(external);
                } else {
                    Phone phone = constructPhonesFromLineParts(tokens);
                    resources.add(phone);
                }
            } else {
                logger.error("Not sure what we're uploading (Phones, Users, or External SIP or Telephone numbers");
            }
        }

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
                throw new IllegalArgumentException("Email address is invalid: " + tokens[3]);
            }

            user.setEmail(tokens[3].trim());
            if (tokens.length >= 5) {
                user.setAddVoicemail("y".equalsIgnoreCase(tokens[4].trim()));
                user.setShouldSendEmail(tokens.length >= 6 && "y".equalsIgnoreCase(tokens[5].trim()));
            }

            return user;
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

        private Phone constructPhonesFromLineParts(String[] tokens) {
            Phone phone = new Phone();
            boolean valid = false;
            phone.setMacAddress(tokens[0].trim());
            phone.setMake(tokens[1].trim());
            phone.setModel(tokens[2].trim());
            phone.setGmtOffset(tokens[3].trim());
            phone.setNatKeepalive(tokens.length >= 5 && tokens[4].trim().equalsIgnoreCase("Y"));
            phone.setOrganizationWebPassword(tokens.length == 6 && tokens[5].trim().equalsIgnoreCase("Y"));

            if (StringUtils.isEmpty(phone.getMacAddress())) {
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
    }

    private void parseCsv(File csvFile) throws Exception {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(csvFile)));

        String line = reader.readLine();
        if (line == null) {
            throw new Exception("No data found in file");
        }

        StrTokenizer tokenizer = StrTokenizer.getCSVInstance();
        tokenizer.setIgnoreEmptyTokens(false);
        tokenizer.reset(line);
        String [] tokens = tokenizer.getTokenArray();
        factory.setResourceType(tokens);

        while ((line = reader.readLine()) != null) {
            tokenizer.reset(line);
            if (line.length() > 0) {
                tokens = tokenizer.getTokenArray();
                factory.parse(tokens);
            }
        }

        reader.close();
    }
}