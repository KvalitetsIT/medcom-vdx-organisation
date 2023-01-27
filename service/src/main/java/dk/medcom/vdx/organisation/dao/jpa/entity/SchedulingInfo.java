package dk.medcom.vdx.organisation.dao.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "scheduling_info")
public class SchedulingInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uriWithDomain;
	@ManyToOne
	@JoinColumn(name="organisation_id")
	private Organisation organisation;
	@Enumerated(EnumType.STRING)
	private ProvisionStatus provisionStatus;

	public String getUriWithDomain() {
		return uriWithDomain;
	}
	public void setUriWithDomain(String uriWithDomain) {
		this.uriWithDomain = uriWithDomain;
	}
	public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProvisionStatus getProvisionStatus() {
		return provisionStatus;
	}

	public void setProvisionStatus(ProvisionStatus provisionStatus) {
		this.provisionStatus = provisionStatus;
	}
}
