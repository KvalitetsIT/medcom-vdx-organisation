package dk.medcom.vdx.organisation.dao;

import dk.medcom.vdx.organisation.dao.entity.OrganisationTemplate;

import java.util.Optional;

public interface OrganisationTemplateDao {
    Optional<OrganisationTemplate> findOrganisationTemplate(String organisationCode);
}
