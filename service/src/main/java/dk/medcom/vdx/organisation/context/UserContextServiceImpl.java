package dk.medcom.vdx.organisation.context;

import org.springframework.beans.factory.annotation.Autowired;

public class UserContextServiceImpl implements UserContextService {
	private UserContextFactory userContextFactory;
	
	private UserContext userContext;

	public UserContextServiceImpl(UserContextFactory userContextFactory) {
		this.userContextFactory = userContextFactory;
	}

	public synchronized UserContext getUserContext()  {
		if (userContext == null) {
			userContext = userContextFactory.getUserContext();
		}
		return userContext;
	}
}
