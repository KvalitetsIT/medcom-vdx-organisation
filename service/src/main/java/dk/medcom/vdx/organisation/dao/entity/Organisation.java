package dk.medcom.vdx.organisation.dao.entity;

public class Organisation {
    private Long groupId;
    private Long parentId;
    private Integer poolSize;
    private String groupName;
    private String organisationId;
    private String organisationName;
    private String smsSenderName;
    private String smsCallbackUrl;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getSmsSenderName() {
        return smsSenderName;
    }

    public void setSmsSenderName(String smsSenderName) {
        this.smsSenderName = smsSenderName;
    }

    public String getSmsCallbackUrl() {
        return smsCallbackUrl;
    }

    public void setSmsCallbackUrl(String smsCallbackUrl) {
        this.smsCallbackUrl = smsCallbackUrl;
    }
}
