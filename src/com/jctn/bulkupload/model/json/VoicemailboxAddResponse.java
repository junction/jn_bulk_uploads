package com.jctn.bulkupload.model.json;

/**
 * @author martin
 */
public class VoicemailboxAddResponse extends AbstractJSONResponse {
    static final long serialVersionUID = -7275129185964882884L;
    private Long vmBoxId;
    private Integer mailBox;
    private Integer password;
    
    public Integer getMailBox() {
	return mailBox;
    }
    public void setMailBox(Integer mailBox) {
	this.mailBox = mailBox;
    }
    public Integer getPassword() {
	return password;
    }
    public void setPassword(Integer password) {
	this.password = password;
    }
    public Long getVmBoxId() {
	return vmBoxId;
    }
    public void setVmBoxId(Long vmBoxId) {
	this.vmBoxId = vmBoxId;
    }
}
