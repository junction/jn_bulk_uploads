package com.jnctn.bulkupload.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

public class TelephoneNumberAddress extends ExternalResource implements IUploadable {

    public TelephoneNumberAddress(String username) {
        this.username = username;
        this.name = northAmericanNumberToPhone(username);
        this.username = username.replaceAll("[-\\(\\)]", "");
    }

    private String northAmericanNumberToPhone(String num) {
        num = num.replaceAll("[^\\d]", "");
        String a [] = num.split("^1(\\d{3})(\\d{3})(\\d{4})$");
        if (a.length == 3) {
            return "1-" + a[0] + "-" + a[1] + "-" + a[2];
        }
        return num;
    }

    @Override
    public String toString() {
	return "TelephoneNumberAddress {" +
	    "error=" + (StringUtils.isEmpty(this._error) ? "NONE" : this._error) +
	    ";name=" + this.name +
	    ";username=" + this.username + "}";
    }
}