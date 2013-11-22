package com.jnctn.bulkupload.model.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author martin
 */
public class SessionCreateResponse extends AbstractJSONResponse {
    public static final String ROLE_ACCOUNT_ADMIN = "Account Admin";
    static final long serialVersionUID = 1089672229043161295L;	
    private List<String> roles = new ArrayList<String>(2);
    private String sessionId;

    public void setSessionId(String data) {
        this.sessionId = data;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }
}
