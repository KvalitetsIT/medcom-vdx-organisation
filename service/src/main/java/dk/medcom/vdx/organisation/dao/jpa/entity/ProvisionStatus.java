package dk.medcom.vdx.organisation.dao.jpa.entity;


public enum ProvisionStatus {
	AWAITS_PROVISION(0),
	STARTING_TO_PROVISION(1),
	PROVISION_PROBLEMS(2),
	PROVISIONED_OK(3),
	STARTING_TO_DEPROVISION(4),
	DEPROVISION_PROBLEMS(5),
	DEPROVISION_OK(6);
	
	private final int value;
	
	private ProvisionStatus(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	
}