package dk.medcom.vdx.organisation.context;

import java.util.LinkedList;
import java.util.List;

public class UserContextImpl extends AbstractUserContextImpl {

	protected String userOrganisation;
	protected String userEmail;
	protected List<UserRole> userRoles;
	
	public UserContextImpl(String userOrganisation, String userEmail, List<UserRole> userRoles) {
		this.userOrganisation = userOrganisation;
		this.userEmail = userEmail;
		this.userRoles = userRoles;
	}
	
	@Override
	public String getUserOrganisation() {
		return userOrganisation;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}

	@Override
	public List<UserRole> getUserRoles() {
		return userRoles;
	}
	
	private static List<UserRole> createList(UserRole userRole) {
		List<UserRole> userRoles = new LinkedList<>();
		userRoles.add(userRole);
		return userRoles;
	}
}
