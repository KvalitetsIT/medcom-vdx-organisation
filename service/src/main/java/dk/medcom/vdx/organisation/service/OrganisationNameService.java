package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Groups;
import dk.medcom.vdx.organisation.dao.jpa.OrganisationRepository;
import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class OrganisationNameService implements OrganisationService {
    private final OrganisationRepository organisationRepository;
    private final OrganisationDao organisationDao;
    private final GroupsDao groupsDao;

    public OrganisationNameService(OrganisationRepository organisationRepository, OrganisationDao organisationDao, GroupsDao groupsDao) {
        this.organisationRepository = organisationRepository;
        this.organisationDao = organisationDao;
        this.groupsDao = groupsDao;
    }

    @Override
    public Optional<Organisation> getOrganisationById(String organisationId) {
        return Optional.ofNullable(organisationRepository.findByOrganisationId(organisationId));
    }

    @Override
    @Transactional
    public Organisation createOrganisation(OrganisationCreate organisationCreate) {
        var organisation = getOrganisationById(organisationCreate.organisationCode());

        if(organisation.isEmpty()) {
            var name = organisationCreate.name() != null ? organisationCreate.name() : organisationCreate.organisationCode();

            dk.medcom.vdx.organisation.dao.entity.Organisation parentOrganisation = organisationDao.findOrganisation(organisationCreate.parentOrganisation());
            if(parentOrganisation == null) {
                throw new InvalidDataException("Parent organisation %s not found".formatted(organisationCreate.parentOrganisation()));
            }

            var group = Groups.createInstance(name,2, parentOrganisation.getGroupId(), "system");
            var groupId = groupsDao.insert(group);

            var newOrganisation = new dk.medcom.vdx.organisation.dao.entity.Organisation();
            newOrganisation.setGroupId(groupId);
            newOrganisation.setPoolSize(null);
            newOrganisation.setOrganisationId(organisationCreate.organisationCode());
            newOrganisation.setOrganisationName(name);
            newOrganisation.setSmsSenderName(null);
            newOrganisation.setSmsCallbackUrl(null);

            organisationDao.insert(newOrganisation);

            return getOrganisationById(organisationCreate.organisationCode()).get();
        }
        else {
            throw new InvalidDataException("Organisation %s already exists.".formatted(organisationCreate.organisationCode()));
        }
    }
}
