package dk.medcom.vdx.organisation.service.impl;

import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;
import dk.medcom.vdx.organisation.service.ValidationService;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotInTreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ValidationServiceImpl implements ValidationService {
    private final static Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);

    private final UserContextService userContextService;
    private final OrganisationDao organisationDao;

    public ValidationServiceImpl(UserContextService userContextService, OrganisationDao organisationDao) {
        this.userContextService = userContextService;
        this.organisationDao = organisationDao;
    }

    @Override
    public void validateAncestorList(List<OrganisationGroupJoin> requestOrgAncestorList) {
        var userContextOrganisation = userContextService.getUserContext().getUserOrganisation();
        validateWithAncestorList(userContextOrganisation, requestOrgAncestorList);
    }

    @Override
    public void validateOrganisationByCode(String requestOrg) {
        var userContextOrganisation = userContextService.getUserContext().getUserOrganisation();
        if (requestOrg.equals(userContextOrganisation)) {
            logger.debug("Requested organisation is the same as user context organisation.");
            return;
        }
        var requestOrgAncestorList = organisationDao.findAncestorsOfOrganisation(requestOrg);
        if (requestOrgAncestorList.isEmpty()) {
            logger.warn("Organisation with code {} does not exist.", requestOrg);
            throw new OrganisationNotFoundException("Organisation with code %s not found.".formatted(requestOrg));
        }
        validateWithAncestorList(userContextOrganisation, requestOrgAncestorList);
    }

    @Override
    public void validateOrganisationByGroupId(int groupId) {
        var userContextOrganisation = userContextService.getUserContext().getUserOrganisation();

        var requestOrgAncestorList = organisationDao.findAncestorsOfOrganisation(groupId);
        if (requestOrgAncestorList.isEmpty()) {
            logger.warn("Group with id {} does not exist.", groupId);
            throw new OrganisationNotFoundException("Group with id %s not found.".formatted(groupId));
        }
        validateWithAncestorList(userContextOrganisation, requestOrgAncestorList);
    }

    private void validateWithAncestorList(String userContextOrganisation, List<OrganisationGroupJoin> requestOrgAncestorList) {
        if (requestOrgAncestorList.stream().anyMatch(x -> x.organisationId() != null && x.organisationId().equals(userContextOrganisation))) {
            return;
        }
        logger.warn("User context organisation is not a parent of the requested organisation.");
        throw new OrganisationNotInTreeException("UserContext organisation is not a parent of the requested organisation.");
    }
}
