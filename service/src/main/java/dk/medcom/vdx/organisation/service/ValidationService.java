package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;

import java.util.List;

public interface ValidationService {
    void validateAncestorList(List<OrganisationGroupJoin> requestOrgAncestorList);
    void validateOrganisationByCode(String requestOrg);
    void validateOrganisationByGroupId(int groupId);
}
