package dk.medcom.vdx.organisation.service.model;

import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;

public record OrganisationSimple(String code) {
    public static OrganisationSimple from(OrganisationGroupJoin organisationGroupJoin) {
        return new OrganisationSimple(organisationGroupJoin.organisationId());
    }
}
