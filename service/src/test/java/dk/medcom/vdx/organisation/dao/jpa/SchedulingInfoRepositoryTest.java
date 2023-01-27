package dk.medcom.vdx.organisation.dao.jpa;

import dk.medcom.vdx.organisation.dao.AbstractDaoTest;
import dk.medcom.vdx.organisation.dao.jpa.entity.ProvisionStatus;
import dk.medcom.vdx.organisation.dao.jpa.entity.SchedulingInfo;
import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SchedulingInfoRepositoryTest extends AbstractDaoTest {

    @Resource
    private SchedulingInfoRepository subject;

    @Test
    public void testFindOneByUriWithDomain_PROVISIONED_OK() {
        //Given
        List<String> uris = new ArrayList<>();
        uris.add("1238@test.dk");

        //When
        List<SchedulingInfo> result = subject.findAllByUriWithDomainAndProvisionStatusOk(uris, ProvisionStatus.PROVISIONED_OK);

        //Then
        assertFalse(result.isEmpty());
        SchedulingInfo schedulingInfo = result.get(0);
        assertEquals(uris.get(0), schedulingInfo.getUriWithDomain());
        assertNotNull(schedulingInfo.getOrganisation());
        assertNotNull(schedulingInfo.getOrganisation().getId());
        assertNotNull(schedulingInfo.getOrganisation().getName());
    }

    @Test
    public void testFindOneByUriWithDomain_NotPROVISIONED_OK() {
        //Given
        List<String> uris = new ArrayList<>();
        uris.add("1230@test.dk");

        //When
        List<SchedulingInfo> result = subject.findAllByUriWithDomainAndProvisionStatusOk(uris, ProvisionStatus.PROVISIONED_OK);

        //Then
        assertTrue(result.isEmpty());
    }
}