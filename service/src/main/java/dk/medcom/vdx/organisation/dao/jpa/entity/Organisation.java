package dk.medcom.vdx.organisation.dao.jpa.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "organisation")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organisationId;
    private String name;
    private Integer poolSize;
    private long groupId;
    private String smsSenderName;
    private boolean allowCustomUriWithoutDomain;
    private String smsCallbackUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public String toString() {
        return name;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getSmsSenderName() {
        return smsSenderName;
    }

    public void setSmsSenderName(String smsSenderName) {
        this.smsSenderName = smsSenderName;
    }

    public boolean getAllowCustomUriWithoutDomain() {
        return allowCustomUriWithoutDomain;
    }

    public void setAllowCustomUriWithoutDomain(boolean allowCustomUriWithoutDomain) {
        this.allowCustomUriWithoutDomain = allowCustomUriWithoutDomain;
    }

    public String getSmsCallbackUrl() {
        return smsCallbackUrl;
    }

    public void setSmsCallbackUrl(String smsCallbackUrl) {
        this.smsCallbackUrl = smsCallbackUrl;
    }
}
