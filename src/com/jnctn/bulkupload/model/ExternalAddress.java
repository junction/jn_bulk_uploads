package com.jnctn.bulkupload.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

public class ExternalAddress extends ExternalResource {

    private String foreignAddress = null;

    public ExternalAddress(String username, String foreignAddress) {
        this.username = username;
        this.foreignAddress = foreignAddress;
        if (this.username.indexOf("@") != -1) {
            this.username = this.username.replace("[\\s]", ".").replace("[-]","_");
        }
    }

    public String getForeignAddress() {
        return this.foreignAddress;
    }

    @Override
    public String toString() {
	return "ExternalAddress {" +
	    "error=" + (StringUtils.isEmpty(this._error) ? "NONE" : this._error) +
	    ";foreignaddress=" + this.foreignAddress +
	    ";username=" + this.username + "}";
    }
}