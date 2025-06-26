package dk.medcom.vdx.organisation.controller.v2.mapper;

import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import org.openapitools.model.Organisation;

public class OrganisationMapper {

    public static Organisation internalToExternal(dk.medcom.vdx.organisation.dao.jpa.entity.Organisation input) {
        return new Organisation()
                .name(input.getName())
                .code(input.getOrganisationId())
                .smsSenderName(input.getSmsSenderName())
                .smsCallbackUrl(input.getSmsCallbackUrl())
                .poolSize(input.getPoolSize() == null ? 0 : input.getPoolSize());
    }

    public static OrganisationCreate externalToInternal(org.openapitools.model.OrganisationCreate input, String parentCode) {
        return new OrganisationCreate(input.getCode(), input.getName(), parentCode);
    }
}
