package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationViews;
import dk.medcom.vdx.organisation.dao.jpa.SchedulingInfoRepository;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.dao.jpa.entity.ProvisionStatus;
import dk.medcom.vdx.organisation.dao.jpa.entity.SchedulingInfo;
import dk.medcom.vdx.organisation.dao.entity.ViewGroups;
import org.openapitools.model.OrganisationUriInner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrganisationByUriService {
    private final SchedulingInfoRepository schedulingInfoRepository;
    private final OrganisationViews organisationViews;

    public OrganisationByUriService(SchedulingInfoRepository schedulingInfoRepository,
                                   OrganisationViews organisationViews) {
        this.schedulingInfoRepository = schedulingInfoRepository;
        this.organisationViews = organisationViews;
    }

    public Set<OrganisationUriInner> getOrganisationByUriWithDomain(List<String> uris) {
        Set<OrganisationUriInner> result = new HashSet<>();

        // Schedulerede møderum
        addToResult(uris, result, getOrganisationFromSchedulingInfo(uris), true);

        if (!uris.isEmpty()){
            // Faste møderum
            addToResult(uris, result, getOrganisationFromLongLivedMeetingRooms(uris), false);

            if (!uris.isEmpty()){
                // Registrerede klienter
                addToResult(uris, result, getOrganisationFromRegisteredClients(uris), false);

                if (!uris.isEmpty()){
                    // Domæner
                    addToResult(uris, result, getOrganisationFromDomain(uris), false);
                }
            }
        }
        return result;
    }

    private void addToResult(List<String> uris, Set<OrganisationUriInner> result, Map<String, Organisation> resultFromDb, boolean booked) {
        for (Map.Entry<String, Organisation> entry : resultFromDb.entrySet()) {
            Organisation value = entry.getValue();
            // Add group name
            Optional<String> groupName = organisationViews.getGroupName(value.getGroupId());
            groupName.ifPresent(value::setGroupName);

            result.add(new OrganisationUriInner()
                    .code(value.getOrganisationId())
                    .name(value.getOrganisationName())
                    .groupId(value.getGroupId())
                    .uri(entry.getKey())
                    .booked(booked)
                    .groupName(value.getGroupName()));
            uris.remove(entry.getKey());
        }
    }

    private Map<String, Organisation> getOrganisationFromSchedulingInfo(List<String> uri){
        List<SchedulingInfo> dbResult = schedulingInfoRepository.findAllByUriWithDomainAndProvisionStatusOk(uri, ProvisionStatus.PROVISIONED_OK);

        Map<String, Organisation> result = new HashMap<>();
        for (SchedulingInfo schedulingInfo : dbResult) {
            var org = new Organisation();
            org.setGroupId(schedulingInfo.getOrganisation().getGroupId());
            org.setOrganisationId(schedulingInfo.getOrganisation().getOrganisationId());
            org.setOrganisationName(schedulingInfo.getOrganisation().getName());
            result.put(schedulingInfo.getUriWithDomain(), org);
        }
        return result;
    }

    private Map<String, Organisation> getOrganisationFromLongLivedMeetingRooms(List<String> uris){
        Map<String, Organisation> result = new HashMap<>();
        for (String uri : uris) {
            Optional<Long> groupId = organisationViews.getGroupIdFromLongLivedMeetingRooms(uri);

            if (groupId.isPresent()){
                Organisation organisation = new Organisation();
                organisation.setGroupId(groupId.get());

                getOrganisationFromViewGroup(groupId.get(), organisation);

                result.put(uri, organisation);
            }
        }
        return result;
    }

    private Map<String,Organisation> getOrganisationFromRegisteredClients(List<String> uris) {
        Map<String, Organisation> result = new HashMap<>();
        for (String uri : uris) {
            Optional<Long> groupId = organisationViews.getGroupIdFromRegisteredClients(uri);

            if (groupId.isPresent()){
                Organisation organisation = new Organisation();
                organisation.setGroupId(groupId.get());

                getOrganisationFromViewGroup(groupId.get(), organisation);

                result.put(uri, organisation);
            }
        }
        return result;
    }

    private Map<String, Organisation> getOrganisationFromDomain(List<String> uris) {
        Map<String, Organisation> result = new HashMap<>();
        for (String uri : uris) {
            String domain = uri.substring(uri.indexOf('@') + 1).toLowerCase();

            Optional<Long> groupId = organisationViews.getGroupIdFromDomain(domain);

            if (groupId.isPresent()){
                Organisation organisation = new Organisation();
                organisation.setGroupId(groupId.get());

                getOrganisationFromViewGroup(groupId.get(), organisation);

                result.put(uri, organisation);
            }else {
                groupId = organisationViews.getGroupIdFromDomainLike(domain);

                if (groupId.isPresent()){
                    Organisation organisation = new Organisation();
                    organisation.setGroupId(groupId.get());

                    getOrganisationFromViewGroup(groupId.get(), organisation);

                    result.put(uri, organisation);
                }
            }
        }
        return result;
    }

    private void getOrganisationFromViewGroup(Long groupId, Organisation organisation) {
        Optional<ViewGroups> viewGroup = organisationViews.getOrganisationFromViewGroup(groupId);
        if (viewGroup.isPresent()){
            ViewGroups info = viewGroup.get();
            organisation.setGroupName(info.getGroup_name());
            organisation.setOrganisationId(String.valueOf(info.getOrganisation_id()));
            organisation.setOrganisationName(info.getOrganisation_id_name());
        }
    }
}
