package dk.medcom.vdx.organisation.dao.entity;

public record OrganisationTemplate(Long id, String organisationCode, String name, String parentOrganisationCode) {
    static OrganisationTemplate createInstance(String organisationCode, String name, String parentOrganisationCode) {
        return new OrganisationTemplate(null, organisationCode, name, parentOrganisationCode);
    }
}
