package dk.medcom.vdx.organisation.context;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractUserContextImpl implements UserContext {

	private static final List<UserRole> MEETINGADMIN_TYPE_ROLES = new LinkedList<>();

	static {
		MEETINGADMIN_TYPE_ROLES.add(UserRole.ADMIN);
		MEETINGADMIN_TYPE_ROLES.add(UserRole.MEETING_PLANNER);
	}
	
	@Override
	public boolean hasNoLegalRoles() {
		List<UserRole> userRoles = getUserRoles();
		return (userRoles == null || userRoles.isEmpty());
	}
	
	@Override
	public boolean hasAnyNumberOfRoles(List<UserRole> userRoles) {
		List<UserRole> myUserRoles = getUserRoles();
		if (myUserRoles != null) {
			for (UserRole myUserRole : myUserRoles) {
				for (UserRole userRole : userRoles) {
					if (userRole == myUserRole) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isOrganisationalMeetingAdministrator() {
		return hasAnyNumberOfRoles(MEETINGADMIN_TYPE_ROLES);
	}
	
	@Override
	public boolean hasOnlyRole(UserRole role) {
		List<UserRole> userRoles = getUserRoles();
		if (role != null && userRoles != null && userRoles.size() == 1) {
			return userRoles.get(0) == role;
		}
		return false;
	}
}
