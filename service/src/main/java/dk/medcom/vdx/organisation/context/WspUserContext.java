package dk.medcom.vdx.organisation.context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class WspUserContext implements UserContextFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(WspUserContext.class);

	private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	private String sessionDataHttpHeaderInput;
	private String userServiceTokenAttributeOrganisation;
	private String userServiceTokenAttributeEmail;
	private String userServiceTokenAttributeUserRole;
	private String mappingRoleProvisioner;
	private String mappingRoleAdmin;
	private String mappingRoleUser;
	private String mappingRoleMeetingPlanner;

	public WspUserContext(String sessionDataHttpHeaderInput,
						  String userServiceTokenAttributeOrganisation,
						  String userServiceTokenAttributeEmail,
						  String userServiceTokenAttributeUserRole,
						  String mappingRoleProvisioner,
						  String mappingRoleAdmin,
						  String mappingRoleUser,
						  String mappingRoleMeetingPlanner) {

		this.sessionDataHttpHeaderInput = sessionDataHttpHeaderInput;
		this.userServiceTokenAttributeOrganisation = userServiceTokenAttributeOrganisation;
		this.userServiceTokenAttributeEmail = userServiceTokenAttributeEmail;
		this.userServiceTokenAttributeUserRole = userServiceTokenAttributeUserRole;
		this.mappingRoleProvisioner = mappingRoleProvisioner;
		this.mappingRoleAdmin = mappingRoleAdmin;
		this.mappingRoleUser = mappingRoleUser;
		this.mappingRoleMeetingPlanner = mappingRoleMeetingPlanner;
	}

	@Override
	public UserContext getUserContext() {
		SessionData sessionData = getSessionData();

		if(sessionData == null) {
			return null;
		}

		String organisationId = sessionData.getUserAttribute(userServiceTokenAttributeOrganisation);
		String email = sessionData.getUserAttribute(userServiceTokenAttributeEmail);
		List<UserRole> userRoles = getUserRoles(sessionData);
		return new UserContextImpl(organisationId, email, userRoles);
	}

	private List<UserRole> getUserRoles(SessionData sessionData) {
		List<UserRole> userRoles = new LinkedList<>();

		List<String> userRoleStrList = sessionData.getUserAttributes(userServiceTokenAttributeUserRole);
		LOGGER.debug("User role is: " + userRoleStrList );
		LOGGER.debug("Map values are: Provisioner: " + mappingRoleProvisioner + " Admin: " + mappingRoleAdmin + " User: " + mappingRoleUser + " Meeting Planner: " + mappingRoleMeetingPlanner);

		if (userRoleStrList != null && userRoleStrList.size() > 0) {
			for (String userRoleStr : userRoleStrList) {
				if (userRoleStr.equals(mappingRoleProvisioner)) {
					if ((sessionData.getUserAttribute(userServiceTokenAttributeOrganisation) != null) && (sessionData.getUserAttribute(userServiceTokenAttributeEmail) != null) ) {
						LOGGER.debug("Provisioner changed to provisioner_user. Because of organisation and email: " +  sessionData.getUserAttribute(userServiceTokenAttributeOrganisation) + " and " + sessionData.getUserAttribute(userServiceTokenAttributeEmail));
						userRoles.add(UserRole.PROVISIONER_USER);
					} else {
						userRoles.add(UserRole.PROVISIONER);
					}
				} else if (userRoleStr.equals(mappingRoleAdmin)) {
					userRoles.add(UserRole.ADMIN);
				} else if (userRoleStr.equals(mappingRoleUser)) {
					userRoles.add(UserRole.USER);
				} else if (userRoleStr.equals(mappingRoleMeetingPlanner)) {
					userRoles.add(UserRole.MEETING_PLANNER);
				} else { 
					LOGGER.debug("Userrole unknown  "+userRoleStr+" ...ignoring");
				}
			}
		} else {
			LOGGER.error("Attributes from token does not contain role (looking for "+userServiceTokenAttributeUserRole+")");
		}
		return userRoles;
	}

	public SessionData getSessionData() {
		LOGGER.debug("Getting session data from header.");
		String sessionDataFromHeader = getSessionDataFromHeader();
		if (sessionDataFromHeader != null && !sessionDataFromHeader.equals("")) {
			return parseSessionDataValue(sessionDataFromHeader);
		}

		LOGGER.info("Session data could not be found in header.");
		return null;

	}

	public SessionData parseSessionDataValue(String encoded) {
		LOGGER.debug("Parsing session data");
		String decoded = "";
		try {
			LOGGER.debug("Decoding session data.");
			decoded = new String(Base64.getDecoder().decode(encoded));
		} catch (IllegalArgumentException e) {
			LOGGER.error("Failed to decode headervalue: "+encoded);
			return null;
		}
	    try {
			LOGGER.debug("Parsing session data.");
			SessionData sessionData = mapper.readValue(decoded, SessionData.class);
			if (!sessionData.containsUserAttributes()) {
				LOGGER.debug("Session data does not contain user attributes.");
				return null;
			}
			return sessionData;
		} catch (IOException e) {
			LOGGER.error("Failed to parse headervalue: "+decoded, e);
			return null;
		}
		
	}
	
	private String getSessionDataFromHeader() {
		if (sessionDataHttpHeaderInput != null && !sessionDataHttpHeaderInput.equals("")) {
			LOGGER.debug("Trying to get session data from HTTP header: " + sessionDataHttpHeaderInput);
			HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			return servletRequest.getHeader(sessionDataHttpHeaderInput);
		}
		LOGGER.debug("Session data not found in HTTP header");
		return null;
	}
}
