package com.jnctn.bulkupload.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

public class ExternalAddress extends ExternalResource {

    private String foreignAddress = null;
    private String extension = null;
    private String name = null;

    public ExternalAddress(String name, String foreignAddress) {
        this.name = name;
        this.username = name;
        this.foreignAddress = foreignAddress;
        this.username = this.username.replaceAll("\\s+", ".").replaceAll("[-]","_");
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getName() {
        return this.name;
    }

    public String getForeignAddress() {
        return this.foreignAddress;
    }

    @Override
    public String toString() {
	return "ExternalAddress {" +
	    "error=" + (StringUtils.isEmpty(this._error) ? "NONE" : this._error) +
	    ";foreignaddress=" + this.foreignAddress +
            ";name=" + this.name +
	    ";username=" + this.username + "}";
    }
}