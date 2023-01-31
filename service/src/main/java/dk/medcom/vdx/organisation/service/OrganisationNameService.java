package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.OrganisationTemplateDao;
import dk.medcom.vdx.organisation.dao.entity.Groups;
import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.jpa.OrganisationRepository;
import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class OrganisationNameService implements OrganisationService {
    private final OrganisationRepository organisationRepository;
    private final OrganisationTemplateDao organisationTemplateDao;
    private final OrganisationDao organisationDao;
    private final GroupsDao groupsDao;

    public OrganisationNameService(OrganisationRepository organisationRepository, OrganisationTemplateDao organisationTemplateDao, OrganisationDao organisationDao, GroupsDao groupsDao) {
        this.organisationRepository = organisationRepository;
        this.organisationTemplateDao = organisationTemplateDao;
        this.organisationDao = organisationDao;
        this.groupsDao = groupsDao;
    }

    @Override
    public Optional<Organisation> getOrganisationById(String organisationId) {
        return Optional.ofNullable(organisationRepository.findByOrganisationId(organisationId));
    }

    @Override
    @Transactional
    public Optional<Organisation> getOrganisationOrCreateFromTemplate(String code) {
        var organisation = getOrganisationById(code);

        if(organisation.isEmpty()) {
            var organisationTemplate = organisationTemplateDao.findOrganisationTemplate(code);

            if(organisationTemplate.isEmpty()) {
                return Optional.empty();
            }
            else {
                var o = organisationTemplate.get();
                var parentOrganisation = organisationDao.findOrganisation(o.parentOrganisationCode());

                var group = Groups.createInstance(o.name(),2, parentOrganisation.getGroupId(), "system");
                var groupId = groupsDao.insert(group);

                var newOrganisation = new dk.medcom.vdx.organisation.dao.entity.Organisation();
                newOrganisation.setGroupId(groupId);
                newOrganisation.setPoolSize(null);
                newOrganisation.setOrganisationId(code);
                newOrganisation.setOrganisationName(o.name());
                newOrganisation.setSmsSenderName(null);
                newOrganisation.setSmsCallbackUrl(null);

                return getOrganisationById(code);
            }
        }
        else {
            return organisation;
        }
    }
}
