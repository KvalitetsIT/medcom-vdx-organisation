package dk.medcom.vdx.organisation.dao;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;

import java.util.List;
import java.util.Optional;

public interface OrganisationDao {
    Organisation findOrganisation(String code);

    Optional<Organisation> findOrganisationByGroupId(long groupId);

    List<Organisation> findOrganisationByParentId(long groupId);

    long insert(Organisation newOrganisation);

    boolean update(Organisation organisation);

    Organisation findOrganisationByHistoryApiKey(String historyApiKey);

    List<OrganisationGroupJoin> findDescendantsOfOrganisation(String code);
}
