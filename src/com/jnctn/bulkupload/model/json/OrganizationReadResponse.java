package com.jnctn.bulkupload.model.json;

/**
 * @author martin
 */
public class OrganizationReadResponse extends AbstractJSONResponse {
    static final long serialVersionUID = -8504270493297944972L;
    private Long orgId;

    public Long getOrgId() {
	return orgId;
    }
    public void setOrgId(Long orgId) {
	this.orgId = orgId;
    }
}
