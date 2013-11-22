package com.jnctn.bulkupload.model.json;

/**
 * Wrapper for user-alias aka, the user's extension. Currently only stores the
 * extension number/string.
 *
 * @author martin
 */
public class UserAliasAddResponse extends AbstractJSONResponse {
    static final long serialVersionUID = 5905361577138270015L;
    private String extension;

    public String getExtension() {
	return extension;
    }
    
    public void setExtension(String extension) {
	this.extension = extension;
    }
}
