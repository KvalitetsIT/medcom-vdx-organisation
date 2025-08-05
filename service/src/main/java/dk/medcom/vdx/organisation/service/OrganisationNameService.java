package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Groups;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.exception.DaoException;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import dk.medcom.vdx.organisation.service.model.OrganisationCreateBasic;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import dk.medcom.vdx.organisation.service.model.OrganisationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class OrganisationNameService implements OrganisationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationNameService.class);

    private final OrganisationDao organisationDao;
    private final GroupsDao groupsDao;

    public OrganisationNameService(OrganisationDao organisationDao, GroupsDao groupsDao) {
        this.organisationDao = organisationDao;
        this.groupsDao = groupsDao;
    }

    @Override
    public Optional<Organisation> getOrganisationById(String organisationId) {
        return Optional.ofNullable(organisationDao.findOrganisation(organisationId));
    }

    @Override
    @Transactional
    public Organisation createOrganisationBasic(OrganisationCreateBasic organisationCreate) {
        var organisation = new OrganisationCreate(
                organisationCreate.parentOrganisation(),
                organisationCreate.organisationCode(),
                organisationCreate.name(),
                null,
                null,
                false,
                null,
                null,
                null,
                null);
        return createOrganisation(organisation);
    }

    @Override
    @Transactional
    public Organisation createOrganisation(OrganisationCreate organisationCreate) {
        var organisation = getOrganisationById(organisationCreate.organisationCode());

        if(organisation.isEmpty()) {
            var name = organisationCreate.name() != null ? organisationCreate.name() : organisationCreate.organisationCode();

            Organisation parentOrganisation = organisationDao.findOrganisation(organisationCreate.parentOrganisation());
            if(parentOrganisation == null) {
                throw new InvalidDataException("Parent organisation %s not found".formatted(organisationCreate.parentOrganisation()));
            }

            var group = Groups.createInstance(name,2, parentOrganisation.getGroupId(), "system");
            var groupId = groupsDao.insert(group);

            var newOrganisation = mapNewOrganisation(organisationCreate, groupId, name);

            organisationDao.insert(newOrganisation);

            return getOrganisationById(organisationCreate.organisationCode()).orElseThrow(() -> {
                logger.warn("Failed to find organisation {} after creation.", newOrganisation.getOrganisationId());
                return new DaoException("Failed to find organisation %s after creation.".formatted(newOrganisation.getOrganisationId()));
            });
        }
        else {
            throw new InvalidDataException("Organisation %s already exists.".formatted(organisationCreate.organisationCode()));
        }
    }

    @Override
    public Organisation updateOrganisation(String organisationCode, OrganisationUpdate organisationUpdate) {
        var update = mapUpdatedOrganisation(organisationCode, organisationUpdate);

        var updateRes = organisationDao.update(update);
        var updatedOrg = getOrganisationById(organisationCode);

        if (updatedOrg.isPresent() && updateRes) {
            return updatedOrg.get();
        } else if (updatedOrg.isEmpty()) {
            logger.warn("Organisation with code {} not found in db.", organisationCode);
            throw new OrganisationNotFoundException("Organisation with code %s not found.".formatted(organisationCode));
        } else {
            logger.warn("Failed to update organisation with code {} in db.", organisationCode);
            throw new DaoException("Failed to update organisation with code %s.".formatted(organisationCode));
        }
    }

    private static Organisation mapNewOrganisation(OrganisationCreate organisationCreate, long groupId, String name) {
        var newOrganisation = new Organisation();
        newOrganisation.setGroupId(groupId);
        newOrganisation.setOrganisationId(organisationCreate.organisationCode());
        newOrganisation.setOrganisationName(name);
        newOrganisation.setPoolSize(organisationCreate.poolSize());
        newOrganisation.setSmsSenderName(organisationCreate.smsSenderName());
        newOrganisation.setAllowCustomUriWithoutDomain(organisationCreate.allowCustomUriWithoutDomain());
        newOrganisation.setSmsCallbackUrl(organisationCreate.smsCallbackUrl());
        newOrganisation.setHistoryApiKey(organisationCreate.historyApiKey());
        newOrganisation.setDeviceWebhookEndpoint(organisationCreate.deviceWebhookEndpoint());
        newOrganisation.setDeviceWebhookEndpointKey(organisationCreate.deviceWebhookEndpointKey());
        return newOrganisation;
    }

    private static Organisation mapUpdatedOrganisation(String organisationCode, OrganisationUpdate organisationUpdate) {
        var update = new Organisation();
        update.setOrganisationId(organisationCode);
        update.setPoolSize(organisationUpdate.poolSize());
        update.setSmsSenderName(organisationUpdate.smsSenderName());
        update.setAllowCustomUriWithoutDomain(organisationUpdate.allowCustomUriWithoutDomain());
        update.setSmsCallbackUrl(organisationUpdate.smsCallbackUrl());
        update.setHistoryApiKey(organisationUpdate.historyApiKey());
        update.setDeviceWebhookEndpoint(organisationUpdate.deviceWebhookEndpoint());
        update.setDeviceWebhookEndpointKey(organisationUpdate.deviceWebhookEndpointKey());
        return update;
    }
}
