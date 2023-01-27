package dk.medcom.vdx.organisation.dao;

import dk.medcom.vdx.organisation.dao.entity.Organisation;

public interface OrganisationDao {
    Organisation findOrganisation(String code);

    Organisation findOrganisationByGroupId(long groupId);
}
