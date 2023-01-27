package dk.medcom.vdx.organisation.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SessionData {

	public Map<String,List<String>> UserAttributes;

	public Map<String, List<String>> getUserAttributes() {
		return UserAttributes;
	}

	public void setUserAttributes(Map<String, List<String>> userAttributes) {
		UserAttributes = userAttributes;
	}

	public List<String> getUserAttributes(String userAttribute) {
		List<String> result = new LinkedList<String>();
		if (UserAttributes != null && UserAttributes.containsKey(userAttribute)) {
			List<String> ual = UserAttributes.get(userAttribute);
			if (ual != null && ual.size() > 0) {
				result.addAll(ual);
			}
		}
		return result;
	}

	
	public String getUserAttribute(String userAttribute) {
		if (UserAttributes != null && UserAttributes.containsKey(userAttribute)) {
			if (UserAttributes.get(userAttribute).size() > 0) {
				return UserAttributes.get(userAttribute).get(0);
			}
		}
		return null;
	}
	
	public boolean containsUserAttributes() {
		return UserAttributes != null;
	}
}
