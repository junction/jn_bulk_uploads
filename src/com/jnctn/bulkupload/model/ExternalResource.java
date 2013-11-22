package com.jnctn.bulkupload.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

public abstract class ExternalResource implements IUploadable {

    protected String _error;
    protected String username;
    protected String name;

    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return this.name;
    }

    public void setError(String errorString) {
	this._error = errorString;
    }

    public String getError() {
	return _error;
    }
}