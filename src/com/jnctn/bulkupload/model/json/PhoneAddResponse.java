package com.jnctn.bulkupload.model.json;

/**
 * @author martin
 */
public class PhoneAddResponse extends AbstractJSONResponse {
  //static final long serialVersionUID = 9101829643107374145L;
    private String username;
    private Long userId;

    public Long getUserId() {
	return userId;
    }
    public void setUserId(Long userId) {
	this.userId = userId;
    }
    public String getUsername() {
	return username;
    }
    public void setUsername(String username) {
	this.username = username;
    }
}
