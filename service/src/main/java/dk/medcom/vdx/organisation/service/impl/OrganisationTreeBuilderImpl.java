package dk.medcom.vdx.organisation.service.impl;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import org.openapitools.model.Organisationtree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganisationTreeBuilderImpl implements OrganisationTreeBuilder {
    public Organisationtree buildOrganisationTree(List<Organisation> organisationList) {
        if (organisationList == null || organisationList.isEmpty()) {
            return null;
        }

        Long rootGroupId = null;
        Map<Long, Organisationtree> treeMap = new HashMap<>();
        for (Organisation organisation : organisationList) {
            Organisationtree tree = mapOrganisationTree(organisation);
            treeMap.merge(organisation.getGroupId(), tree, this::merge);

            Organisationtree parentOrganisationTreeDto;
            if(organisation.getParentId() != null) {
                parentOrganisationTreeDto = treeMap.getOrDefault(organisation.getParentId(), new Organisationtree());
                parentOrganisationTreeDto.addChildrenItem(tree);
                treeMap.put(organisation.getParentId(), parentOrganisationTreeDto);
            }
            else {
                rootGroupId = organisation.getGroupId();
            }
        }

        return treeMap.get(rootGroupId);
    }

    private Organisationtree merge(Organisationtree existingOrganisation, Organisationtree organisation) {
        if(existingOrganisation.getChildren() != null) {
            existingOrganisation.getChildren().forEach(x -> organisation.addChildrenItem(x));
        }

        return organisation;
    }

    private Organisationtree mapOrganisationTree(Organisation organisation) {
        Organisationtree organisationTreeDto = new Organisationtree();
        organisationTreeDto.setCode(organisation.getOrganisationId() != null ? organisation.getOrganisationId() : organisation.getGroupId().toString());
        organisationTreeDto.setName(organisation.getOrganisationName() != null ? organisation.getOrganisationName() : organisation.getGroupName());
        organisationTreeDto.setPoolSize(organisation.getPoolSize() != null ? organisation.getPoolSize() : 0);
        organisationTreeDto.setSmsCallbackUrl(organisation.getSmsCallbackUrl());
        organisationTreeDto.setSmsSenderName(organisation.getSmsSenderName());

        return organisationTreeDto;
    }
}
