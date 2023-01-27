package dk.medcom.vdx.organisation.service.impl;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganisationTreeServiceImpl implements OrganisationTreeService {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationTreeServiceImpl.class);

    private final OrganisationDao organisationDao;

    public OrganisationTreeServiceImpl(OrganisationDao organisationDao) {
        this.organisationDao = organisationDao;
    }

    @Override
    public Optional<List<Organisation>> findOrganisations(String code) {
        var organisation = organisationDao.findOrganisation(code);
        if(organisation == null) {
            logger.debug("Organisation {} not found in database.", code);
            return Optional.empty();
        }

        var organisationList = new ArrayList<Organisation>();
        organisationList.add(organisation);
        while(organisation.getParentId() != null) {
            organisation = organisationDao.findOrganisationByGroupId(organisation.getParentId());
            organisationList.add(organisation);
        }

        return Optional.of(organisationList);
    }
}
