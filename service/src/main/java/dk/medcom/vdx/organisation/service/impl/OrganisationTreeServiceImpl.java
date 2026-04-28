package dk.medcom.vdx.organisation.service.impl;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.vdx.organisation.service.ValidationService;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import dk.medcom.vdx.organisation.service.model.OrganisationModel;
import dk.medcom.vdx.organisation.service.model.OrganisationSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OrganisationTreeServiceImpl implements OrganisationTreeService {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationTreeServiceImpl.class);

    private final ValidationService validationService;
    private final OrganisationDao organisationDao;

    public OrganisationTreeServiceImpl(ValidationService validationService, OrganisationDao organisationDao) {
        this.validationService = validationService;
        this.organisationDao = organisationDao;
    }

    @Override
    public Optional<List<Organisation>> findOrganisations(String code) {
        var organisation = organisationDao.findOrganisation(code);
        return getOrganisations(code, organisation);
    }

    @Override
    public Optional<List<Organisation>> findOrganisations(String apiKeyType, String apiKey) {
        if(apiKeyType.equals("history")) {
            var organisation = organisationDao.findOrganisationByHistoryApiKey(apiKey);
            if(organisation == null) {
                logger.info("Organisation not found in database.");
                return Optional.empty();
            }

            return getOrganisations(organisation.getOrganisationId(), organisation);

        }

        return Optional.empty();
    }

    @Override
    public Optional<List<Organisation>> getByGroupId(int groupId) {
        var organisation = organisationDao.findOrganisationByGroupId(groupId);

        if(organisation.isEmpty()) {
            return Optional.empty();
        }

        return getOrganisations(organisation.get().getOrganisationId(), organisation.get());
    }

    @Override
    public List<Organisation> findChildrenByGroupId(int groupId) {
        var organisation = organisationDao.findOrganisationByGroupId(groupId);
        if(organisation.isEmpty()) {
            logger.info("Organisation not found in database.");
            return Collections.emptyList();
        }

        var organisations = new ArrayList<Organisation>();
        organisations.add(organisation.get());
        organisations.addAll(children(organisation.get().getGroupId().intValue()));

        return organisations;
    }

    @Override
    public List<Organisation> findChildrenByOrganisationCode(String code) {
        var organisation = organisationDao.findOrganisation(code);
        if(organisation == null) {
            logger.info("Organisation not found in database.");
            return Collections.emptyList();
        }

        var organisations = new ArrayList<Organisation>();
        organisations.add(organisation);
        organisations.addAll(children(organisation.getGroupId().intValue()));

        return organisations;
    }

    @Override
    public List<OrganisationModel> findAncestorsByCode(String code) {
        var organisations = organisationDao.findAncestorsOfOrganisation(code);
        if (organisations.isEmpty()) {
            logger.warn("Organisation with code {} not found.", code);
            throw new OrganisationNotFoundException("Organisation with code %s not found.".formatted(code));
        }
        validationService.validateAncestorList(organisations);

        return organisations.stream().map(OrganisationModel::from).toList();
    }

    @Override
    public List<OrganisationModel> findAncestorsByGroupId(int groupId) {
        var organisations = organisationDao.findAncestorsOfOrganisation(groupId);
        if (organisations.isEmpty()) {
            logger.warn("Organisation with group id {} not found.", groupId);
            throw new OrganisationNotFoundException("Organisation with group id %s not found.".formatted(groupId));
        }
        validationService.validateAncestorList(organisations);

        return organisations.stream().map(OrganisationModel::from).toList();
    }

    @Override
    public List<OrganisationModel> findAncestorsByApiKey(String apiKeyType, String apiKey) {
        if(apiKeyType.equals("history")) {
            var organisations = organisationDao.findAncestorsOfOrganisationByHistoryApiKey(apiKey);
            if(organisations.isEmpty()) {
                logger.warn("Organisation not found in database.");
                throw new OrganisationNotFoundException("Organisation not found in database.");
            }
            validationService.validateAncestorList(organisations);

            return organisations.stream().map(OrganisationModel::from).toList();
        }

        logger.warn("ApiKey is not of valid type.");
        throw new InvalidDataException("ApiKey is not of valid type.");
    }

    @Override
    public List<OrganisationModel> findDescendantsByGroupId(int groupId) {
        validationService.validateOrganisationByGroupId(groupId);

        var organisations = organisationDao.findDescendantsOfOrganisation(groupId);
        if (organisations.isEmpty()) {
            logger.warn("Organisation with group id {} not found.", groupId);
            throw new OrganisationNotFoundException("Organisation with group id %s not found.".formatted(groupId));
        }

        return organisations.stream().map(OrganisationModel::from).toList();
    }

    @Override
    public List<OrganisationModel> findDescendantsByCode(String code) {
        validationService.validateOrganisationByCode(code);

        var organisations = organisationDao.findDescendantsOfOrganisation(code);
        if (organisations.isEmpty()) {
            logger.warn("Organisation with code {} not found.", code);
            throw new OrganisationNotFoundException("Organisation with code %s not found.".formatted(code));
        }

        return organisations.stream().map(OrganisationModel::from).toList();
    }

    @Override
    public List<OrganisationSimple> findDescendantsOfOrganisation(String code) {
        validationService.validateOrganisationByCode(code);

        var descendants = organisationDao.findDescendantsOfOrganisation(code);
        if (descendants.isEmpty()) {
            logger.warn("Organisation with code {} not found in db.", code);
            throw new OrganisationNotFoundException("Organisation with code %s not found.".formatted(code));
        }
        return descendants.stream().filter(x -> x.organisationId() != null)
                .map(OrganisationSimple::from).toList();
    }

    private List<Organisation> children(int groupId) {
        var childOrganisations = organisationDao.findOrganisationByParentId(groupId);
        var result = new ArrayList<>(childOrganisations);
        childOrganisations.forEach(x -> result.addAll(children(x.getGroupId().intValue())));

        return result;
    }

    private Optional<List<Organisation>> getOrganisations(String code, Organisation organisation) {
        if(organisation == null) {
            logger.debug("Organisation {} not found in database.", code);
            return Optional.empty();
        }

        var organisationList = new ArrayList<Organisation>();
        organisationList.add(organisation);
        while(organisation.getParentId() != null) {
            organisation = organisationDao.findOrganisationByGroupId(organisation.getParentId()).get();
            organisationList.add(organisation);
        }

        return Optional.of(organisationList);
    }
}