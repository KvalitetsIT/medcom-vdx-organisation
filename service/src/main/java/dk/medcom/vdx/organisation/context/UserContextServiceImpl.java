package dk.medcom.vdx.organisation.context;

public class UserContextServiceImpl implements UserContextService {
	private final UserContextFactory userContextFactory;
	
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

	@Override
	public void setUserContext(UserContext userContext) {
        if (this.userContext == null) {
            this.userContext = userContext;
        }
    }
}
