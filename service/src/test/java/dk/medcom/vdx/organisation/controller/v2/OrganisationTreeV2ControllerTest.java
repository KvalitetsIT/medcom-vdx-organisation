package dk.medcom.vdx.organisation.controller.v2;

import dk.medcom.vdx.organisation.controller.exception.BadRequestException;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundV2Exception;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.OrganisationTreeForApiKey;
import org.openapitools.model.Organisationtree;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrganisationTreeV2ControllerTest {

    private OrganisationTreeV2Controller organisationTreeV2Controller;
    private OrganisationTreeService organisationTreeService;
    private OrganisationTreeBuilder organisationTreeBuilder;

    @BeforeEach
    public void setup() {
        organisationTreeService = Mockito.mock(OrganisationTreeService.class);
        organisationTreeBuilder = Mockito.mock(OrganisationTreeBuilder.class);
        organisationTreeV2Controller = new OrganisationTreeV2Controller(organisationTreeService, organisationTreeBuilder);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(organisationTreeService, organisationTreeBuilder);
    }

    @Test
    public void testGetOrganisationTreeSlash() {
        var organisationCode = "some/org/with/Slash";
        var request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn("/services/v2/organisationtree/some/org/with/Slash");

        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(organisationCode);
        Mockito.when(organisationTreeService.findOrganisations(organisationCode)).thenReturn(Optional.of(organisationList));
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList)).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.getOrganisationTreeSlash(request);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).findOrganisations(organisationCode);
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationTreeForApiKeyPost() {
        var input = new OrganisationTreeForApiKey(randomString(), randomString());
        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(randomString());

        Mockito.when(organisationTreeService.findOrganisations(input.getApiKeyType(), input.getApiKey())).thenReturn(Optional.of(organisationList));
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList)).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.servicesV2OrganisationTreeForApiKeyPost(input);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).findOrganisations(input.getApiKeyType(), input.getApiKey());
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationTreeForApiKeyPostNotFound() {
        var input = new OrganisationTreeForApiKey(randomString(), randomString());
        Mockito.when(organisationTreeService.findOrganisations(input.getApiKeyType(), input.getApiKey())).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationTreeV2Controller.servicesV2OrganisationTreeForApiKeyPost(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Request does not identify an organisation.", expectedException.getMessage());

        Mockito.verify(organisationTreeService).findOrganisations(input.getApiKeyType(), input.getApiKey());
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByCode() {
        var organisationCode = randomString();
        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(organisationCode);

        Mockito.when(organisationTreeService.findChildrenByOrganisationCode(organisationCode)).thenReturn(organisationList);
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList, organisationList.getFirst().getGroupId())).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.servicesV2OrganisationtreeChildrenGet(organisationCode, null);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).findChildrenByOrganisationCode(organisationCode);
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList, organisationList.getFirst().getGroupId());
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByCodeNotFound() {
        var organisationCode = randomString();

        Mockito.when(organisationTreeService.findChildrenByOrganisationCode(organisationCode)).thenReturn(List.of());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeChildrenGet(organisationCode, null));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Organisation tree with organisation code " + organisationCode + " not found.", expectedException.getMessage());

        Mockito.verify(organisationTreeService).findChildrenByOrganisationCode(organisationCode);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByGroupId() {
        var organisationGroup = 123;
        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(randomString());

        Mockito.when(organisationTreeService.findChildrenByGroupId(organisationGroup)).thenReturn(organisationList);
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList, (long) organisationGroup)).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.servicesV2OrganisationtreeChildrenGet(null, organisationGroup);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).findChildrenByGroupId(organisationGroup);
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList, (long) organisationGroup);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByGroupIdNotFound() {
        var organisationGroup = 123;

        Mockito.when(organisationTreeService.findChildrenByGroupId(organisationGroup)).thenReturn(List.of());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeChildrenGet(null, organisationGroup));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Organisation tree with group id " + organisationGroup + " not found.", expectedException.getMessage());

        Mockito.verify(organisationTreeService).findChildrenByGroupId(organisationGroup);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetNoCodeOrGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeChildrenGet(null, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Either organisationCode or groupId must be set.", exception.getMessage());

        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetBothCodeAndGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeChildrenGet(randomString(), 1));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("organisationCode and groupId is mutually exclusive.", exception.getMessage());

        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeCodeGet() {
        var organisationCode = randomString();

        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(organisationCode);
        Mockito.when(organisationTreeService.findOrganisations(organisationCode)).thenReturn(Optional.of(organisationList));
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList)).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.servicesV2OrganisationtreeCodeGet(organisationCode);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).findOrganisations(organisationCode);
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeCodeGetNotFound() {
        var input = randomString();
        Mockito.when(organisationTreeService.findOrganisations(input)).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeCodeGet(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("The code: "+ input +" does not identify an organisation", expectedException.getMessage());

        Mockito.verify(organisationTreeService).findOrganisations(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeGetByCode() {
        var organisationCode = randomString();
        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(randomString());

        Mockito.when(organisationTreeService.findOrganisations(organisationCode)).thenReturn(Optional.of(organisationList));
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList)).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.servicesV2OrganisationtreeGet(organisationCode, null);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).findOrganisations(organisationCode);
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeGetByCodeNotFound() {
        var organisationCode = randomString();

        Mockito.when(organisationTreeService.findOrganisations(organisationCode)).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeGet(organisationCode, null));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("The code: " + organisationCode + " does not identify an organisation", expectedException.getMessage());

        Mockito.verify(organisationTreeService).findOrganisations(organisationCode);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeGetByGroupId() {
        var organisationGroup = 123;
        var organisationList = List.of(randomOrganisation(), randomOrganisation(), randomOrganisation(), randomOrganisation());
        var buildTreeOutput = randomOrganisationTree(randomString());

        Mockito.when(organisationTreeService.getByGroupId(organisationGroup)).thenReturn(Optional.of(organisationList));
        Mockito.when(organisationTreeBuilder.buildOrganisationTree(organisationList)).thenReturn(buildTreeOutput);

        var resultEntity = organisationTreeV2Controller.servicesV2OrganisationtreeGet(null, organisationGroup);
        assertNotNull(resultEntity);
        assertEquals(200, resultEntity.getStatusCode().value());

        assertEquals(buildTreeOutput, resultEntity.getBody());

        Mockito.verify(organisationTreeService).getByGroupId(organisationGroup);
        Mockito.verify(organisationTreeBuilder).buildOrganisationTree(organisationList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeGetByGroupIdNotFound() {
        var organisationGroup = 123;

        Mockito.when(organisationTreeService.getByGroupId(organisationGroup)).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeGet(null, organisationGroup));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("The group Id " + organisationGroup + " does not exist.", expectedException.getMessage());

        Mockito.verify(organisationTreeService).getByGroupId(organisationGroup);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeGetNoCodeOrGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeGet(null, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Either organisationCode or groupId must be set.", exception.getMessage());

        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationtreeGetBothCodeAndGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeV2Controller.servicesV2OrganisationtreeGet("code", 1));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("organisationCode and groupId is mutually exclusive.", exception.getMessage());

        verifyNoMoreInteractions();
    }

    private long count = 0;

    private Organisation randomOrganisation() {
        var organisation = new Organisation();
        organisation.setGroupId(count++);
        organisation.setParentId(count++);
        organisation.setPoolSize((int) count++);
        organisation.setGroupName(randomString());
        organisation.setOrganisationId(randomString());
        organisation.setOrganisationName(randomString());
        organisation.setSmsSenderName(randomString());
        organisation.setSmsCallbackUrl(randomString());
        organisation.setDeviceWebhookEndpoint(randomString());
        organisation.setDeviceWebhookEndpointKey(randomString());

        return organisation;
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Organisationtree randomOrganisationTree(String organisation) {
        var babyTreeOne = createOrganisationTree(randomString(), List.of());
        var babyTreeTwo = createOrganisationTree(randomString(), List.of());

        var childTree = createOrganisationTree(randomString(), List.of(babyTreeOne, babyTreeTwo));

        var teenageTreeOne = createOrganisationTree(randomString(), List.of(childTree));
        var teenageTreeTwo = createOrganisationTree(randomString(), List.of());

        var parentTreeOne = createOrganisationTree(randomString(), List.of(teenageTreeOne));
        var parentTreeTwo = createOrganisationTree(randomString(), List.of(teenageTreeTwo));
        var parentTreeThree = createOrganisationTree(randomString(), List.of());

        return createOrganisationTree(organisation, List.of(parentTreeOne, parentTreeTwo, parentTreeThree));
    }

    private Organisationtree createOrganisationTree(String code, List<Organisationtree> children) {
        return new Organisationtree()
                .code(code)
                .name(randomString())
                .poolSize((int) count++)
                .children(children)
                .smsSenderName(randomString())
                .smsCallbackUrl(randomString())
                .groupId((int) count++)
                .deviceWebhookEndpoint(randomString())
                .deviceWebhookEndpointKey(randomString());
    }
}
