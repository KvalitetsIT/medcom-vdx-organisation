package dk.medcom.vdx.organisation.dao.entity;

public class Organisation {
	private Long id;

	private Long parentOrganisationId;

	private String parentOrganisationCode;

	private String organisationId;

	private String name;

	private Integer poolSize;

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
	
	public Long getParentOrganisationId() {
		return parentOrganisationId;
	}

	public void setParentOrganisationId(Long parentOrganisationId) {
		this.parentOrganisationId = parentOrganisationId;
	}
	
	public String getParentOrganisationCode() {
		return parentOrganisationCode;
	}

	public void setParentOrganisationCode(String parentOrganisationCode) {
		this.parentOrganisationCode = parentOrganisationCode;
	}

	@Override
	public String toString() {
		return name;
	}
}
