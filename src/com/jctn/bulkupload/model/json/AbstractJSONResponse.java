package com.jctn.bulkupload.model.json;

import java.util.Map;
import java.io.Serializable;

/**
 * Represents an abstract JSON-formatted response.
 *
 * @author martin
 */
public abstract class AbstractJSONResponse implements Serializable {
    private Map<String, String> errors;

    static final String NAME_ERRORS    = "Errors";
    static final String NAME_ERROR     = "Error";
    static final String NAME_PARAMETER = "Parameter";
    static final String NAME_CODE      = "Code";
    static final String NAME_MESSAGE   = "Message";

    public Map<String, String> getErrors() {
	return errors;
    }

    public void setErrors(Map<String, String> errors) {
	this.errors = errors;
    }

    public boolean hasError() {
	return errors != null && !errors.isEmpty();
    }
}
