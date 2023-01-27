package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationViews;
import dk.medcom.vdx.organisation.dao.jpa.SchedulingInfoRepository;
import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import dk.medcom.vdx.organisation.dao.jpa.entity.SchedulingInfo;
import dk.medcom.vdx.organisation.dao.entity.ViewGroups;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.OrganisationUriInner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganisationByUriServiceTest {
    private OrganisationByUriService uut;
    private SchedulingInfoRepository schedulingInfoRepositoryMock;
    private OrganisationViews organisationViewsMock;

    @Before
    public void setup(){
        schedulingInfoRepositoryMock = Mockito.mock(SchedulingInfoRepository.class);
        organisationViewsMock = Mockito.mock(OrganisationViews.class);
        uut = new OrganisationByUriService(schedulingInfoRepositoryMock, organisationViewsMock);
    }

    @Test
    @Ignore // missing view view_entities_meetingroom
    public void testMatchInSchedulingInfo(){
        //Given
        String uri = "1234@rooms.vocnf.dk";
        String groupName = "myGroup";

        List<SchedulingInfo> schedulingInfoRepositoryMockReturn = new ArrayList<>();
        SchedulingInfo schedulingInfo = new SchedulingInfo();
        schedulingInfo.setUriWithDomain(uri);
        schedulingInfo.setOrganisation(new Organisation());
        schedulingInfoRepositoryMockReturn.add(schedulingInfo);
        Mockito.when(schedulingInfoRepositoryMock.findAllByUriWithDomainAndProvisionStatusOk(Mockito.any(), Mockito.any())).thenReturn(schedulingInfoRepositoryMockReturn);
        Mockito.when(organisationViewsMock.getGroupName(Mockito.any())).thenReturn(Optional.of(groupName));

        //When
        List<String> uris = new ArrayList<>();
        uris.add(uri);
        List<OrganisationUriInner> result = new ArrayList<>(uut.getOrganisationByUriWithDomain(uris));

        //Then
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(uri, result.get(0).getUri());
        Assert.assertEquals(groupName, result.get(0).getGroupName());
        Mockito.verify(organisationViewsMock, Mockito.never()).getGroupIdFromLongLivedMeetingRooms(Mockito.any());
    }

    @Test
    public void testMatchInSchedulingInfoAndDomain(){
        //Given
        String uri1 = "1234@rooms.vconf.dk";
        String uri2 = "5678@rooms.vconf.dk";
        Long groupId1 = 0L;
        Long groupId2 = 50L;
        String groupName1 = "myGroup";
        String groupName2 = "myGroup2";

        List<SchedulingInfo> schedulingInfoRepositoryMockReturn = new ArrayList<>();
        SchedulingInfo schedulingInfo = new SchedulingInfo();
        schedulingInfo.setUriWithDomain(uri1);
        schedulingInfo.setOrganisation(new Organisation());
        schedulingInfoRepositoryMockReturn.add(schedulingInfo);
        Mockito.when(schedulingInfoRepositoryMock.findAllByUriWithDomainAndProvisionStatusOk(Mockito.any(), Mockito.any())).thenReturn(schedulingInfoRepositoryMockReturn);
        Mockito.when(organisationViewsMock.getGroupName(groupId1)).thenReturn(Optional.of(groupName1));
        Mockito.when(organisationViewsMock.getGroupName(groupId2)).thenReturn(Optional.of(groupName2));
        Mockito.when(organisationViewsMock.getGroupIdFromDomain("rooms.vconf.dk")).thenReturn(Optional.of(groupId2));
        ViewGroups viewGroups = new ViewGroups();
        viewGroups.setOrganisation_id_name(groupName2);
        Mockito.when(organisationViewsMock.getOrganisationFromViewGroup(groupId2)).thenReturn(Optional.of(viewGroups));

        //When
        List<String> uris = new ArrayList<>();
        uris.add(uri1);
        uris.add(uri2);
        List<OrganisationUriInner> result = new ArrayList<>(uut.getOrganisationByUriWithDomain(uris));

        //Then
        Assert.assertEquals(2, result.size());
        var result1 = result.stream().filter(x -> x.getUri().equals(uri1)).findFirst().get();
        var result2 = result.stream().filter(x -> x.getUri().equals(uri2)).findFirst().get();

        Assert.assertEquals(uri1, result1.getUri());
        Assert.assertEquals(groupName1, result1.getGroupName());
        Assert.assertEquals(uri2, result2.getUri());
        Assert.assertEquals(groupName2, result2.getGroupName());
    }
}
