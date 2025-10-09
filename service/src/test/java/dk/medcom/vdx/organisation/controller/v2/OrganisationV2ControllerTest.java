package dk.medcom.vdx.organisation.controller.v2;

import dk.medcom.vdx.organisation.controller.exception.BadRequestException;
import dk.medcom.vdx.organisation.controller.exception.InternalServerErrorException;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundV2Exception;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationByUriService;
import dk.medcom.vdx.organisation.service.OrganisationService;
import dk.medcom.vdx.organisation.service.exception.DaoException;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.OrganisationCreate;
import org.openapitools.model.OrganisationResponse;
import org.openapitools.model.OrganisationUpdate;
import org.openapitools.model.OrganisationUriInner;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class OrganisationV2ControllerTest {
    private OrganisationV2Controller organisationV2Controller;

    private OrganisationService organisationService;
    private OrganisationByUriService organisationByUriService;

    @Before
    public void setup() {
        organisationService = Mockito.mock(OrganisationService.class);
        organisationByUriService = Mockito.mock(OrganisationByUriService.class);

        organisationV2Controller = new OrganisationV2Controller(organisationService, organisationByUriService);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(organisationService, organisationByUriService);
    }

    @Test
    public void testGetOrganisationSlash() {
        var request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn("/services/v2/organisation/some/org/with/Slash");
        var organisation = randomOrganisation();
        Mockito.when(organisationService.getOrganisationById("some/org/with/Slash")).thenReturn(Optional.of(organisation));

        var result = organisationV2Controller.getOrganisationSlash(request);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).getOrganisationById("some/org/with/Slash");
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationCodeGet() {
        var input = randomString();

        var organisation = randomOrganisation();
        Mockito.when(organisationService.getOrganisationById(input)).thenReturn(Optional.of(organisation));

        var result = organisationV2Controller.servicesV2OrganisationCodeGet(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).getOrganisationById(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationCodeGetNoOrganisation() {
        var input = randomString();

        Mockito.when(organisationService.getOrganisationById(input)).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationV2Controller.servicesV2OrganisationCodeGet(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Organisation with id: %s not found".formatted(input), expectedException.getMessage());

        Mockito.verify(organisationService).getOrganisationById(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationCodePut() {
        var organisation = randomString();
        var input = randomOrganisationUpdate();
        var output = randomOrganisation();

        Mockito.when(organisationService.updateOrganisation(Mockito.any(), Mockito.any())).thenReturn(output);

        var result = organisationV2Controller.servicesV2OrganisationCodePut(organisation, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(output, result.getBody());

        Mockito.verify(organisationService).updateOrganisation(Mockito.eq(organisation), Mockito.argThat(x -> {
            assertNotNull(input.getPoolSize());
            assertEquals(input.getPoolSize(), x.poolSize(), 0);
            assertEquals(input.getSmsSenderName(), x.smsSenderName());
            assertEquals(input.getAllowCustomUriWithoutDomain(), x.allowCustomUriWithoutDomain());
            assertEquals(input.getSmsCallbackUrl(), x.smsCallbackUrl());
            assertEquals(input.getHistoryApiKey(), x.historyApiKey());
            assertEquals(input.getDeviceWebhookEndpoint(), x.deviceWebhookEndpoint());
            assertEquals(input.getDeviceWebhookEndpointKey(), x.deviceWebhookEndpointKey());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationCodePutOrganisationNotFound() {
        var organisation = randomString();
        var input = randomOrganisationUpdate();

        Mockito.when(organisationService.updateOrganisation(Mockito.any(), Mockito.any())).thenThrow(new OrganisationNotFoundException("Could not find."));

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationV2Controller.servicesV2OrganisationCodePut(organisation, input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Could not find.", expectedException.getMessage());

        Mockito.verify(organisationService).updateOrganisation(Mockito.eq(organisation), Mockito.argThat(x -> {
            assertNotNull(input.getPoolSize());
            assertEquals(input.getPoolSize(), x.poolSize(), 0);
            assertEquals(input.getSmsSenderName(), x.smsSenderName());
            assertEquals(input.getAllowCustomUriWithoutDomain(), x.allowCustomUriWithoutDomain());
            assertEquals(input.getSmsCallbackUrl(), x.smsCallbackUrl());
            assertEquals(input.getHistoryApiKey(), x.historyApiKey());
            assertEquals(input.getDeviceWebhookEndpoint(), x.deviceWebhookEndpoint());
            assertEquals(input.getDeviceWebhookEndpointKey(), x.deviceWebhookEndpointKey());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationCodePutDaoException() {
        var organisation = randomString();
        var input = randomOrganisationUpdate();

        Mockito.when(organisationService.updateOrganisation(Mockito.any(), Mockito.any())).thenThrow(new DaoException("DB failed."));

        var expectedException = assertThrows(InternalServerErrorException.class, () -> organisationV2Controller.servicesV2OrganisationCodePut(organisation, input));
        assertNotNull(expectedException);
        assertEquals(500, expectedException.getHttpStatus().value());
        assertEquals("DB failed.", expectedException.getMessage());

        Mockito.verify(organisationService).updateOrganisation(Mockito.eq(organisation), Mockito.argThat(x -> {
            assertNotNull(input.getPoolSize());
            assertEquals(input.getPoolSize(), x.poolSize(), 0);
            assertEquals(input.getSmsSenderName(), x.smsSenderName());
            assertEquals(input.getAllowCustomUriWithoutDomain(), x.allowCustomUriWithoutDomain());
            assertEquals(input.getSmsCallbackUrl(), x.smsCallbackUrl());
            assertEquals(input.getHistoryApiKey(), x.historyApiKey());
            assertEquals(input.getDeviceWebhookEndpoint(), x.deviceWebhookEndpoint());
            assertEquals(input.getDeviceWebhookEndpointKey(), x.deviceWebhookEndpointKey());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationGet() {
        var input = randomString();

        var organisation = randomOrganisation();
        Mockito.when(organisationService.getOrganisationById(input)).thenReturn(Optional.of(organisation));

        var result = organisationV2Controller.servicesV2OrganisationGet(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).getOrganisationById(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationGetNoOrganisation() {
        var input = randomString();

        Mockito.when(organisationService.getOrganisationById(input)).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundV2Exception.class, () -> organisationV2Controller.servicesV2OrganisationGet(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Organisation with id: %s not found".formatted(input), expectedException.getMessage());

        Mockito.verify(organisationService).getOrganisationById(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationParentCodePost() {
        var inputParent = randomString();
        var inputCreate = randomOrganisationCreate();

        var organisation = randomOrganisation();
        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenReturn(organisation);

        var result = organisationV2Controller.servicesV2OrganisationParentCodePost(inputParent, inputCreate);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationParentCodePostOnlyRequiredValues() {
        var inputParent = randomString();
        var inputCreate = new OrganisationCreate(randomString());

        var organisation = randomOrganisation();
        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenReturn(organisation);

        var result = organisationV2Controller.servicesV2OrganisationParentCodePost(inputParent, inputCreate);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationParentCodePostInvalidData() {
        var inputParent = randomString();
        var inputCreate = randomOrganisationCreate();

        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenThrow(new InvalidDataException("Message"));

        var expectedException = assertThrows(BadRequestException.class, () -> organisationV2Controller.servicesV2OrganisationParentCodePost(inputParent, inputCreate));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getMessage());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationParentCodePostDaoException() {
        var inputParent = randomString();
        var inputCreate = randomOrganisationCreate();

        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenThrow(new DaoException("Message"));

        var expectedException = assertThrows(InternalServerErrorException.class, () -> organisationV2Controller.servicesV2OrganisationParentCodePost(inputParent, inputCreate));
        assertNotNull(expectedException);
        assertEquals(500, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getMessage());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationPost() {
        var inputParent = randomString();
        var inputCreate = randomOrganisationCreate();

        var organisation = randomOrganisation();
        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenReturn(organisation);

        var result = organisationV2Controller.servicesV2OrganisationPost(inputParent, inputCreate);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationPostOnlyRequiredValues() {
        var inputParent = randomString();
        var inputCreate = new OrganisationCreate(randomString());

        var organisation = randomOrganisation();
        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenReturn(organisation);

        var result = organisationV2Controller.servicesV2OrganisationPost(inputParent, inputCreate);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertOrganisation(organisation, result.getBody());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationPostInvalidData() {
        var inputParent = randomString();
        var inputCreate = randomOrganisationCreate();

        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenThrow(new InvalidDataException("Message"));

        var expectedException = assertThrows(BadRequestException.class, () -> organisationV2Controller.servicesV2OrganisationPost(inputParent, inputCreate));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getMessage());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationPostDaoException() {
        var inputParent = randomString();
        var inputCreate = randomOrganisationCreate();

        Mockito.when(organisationService.createOrganisation(Mockito.any())).thenThrow(new DaoException("Message"));

        var expectedException = assertThrows(InternalServerErrorException.class, () -> organisationV2Controller.servicesV2OrganisationPost(inputParent, inputCreate));
        assertNotNull(expectedException);
        assertEquals(500, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getMessage());

        Mockito.verify(organisationService).createOrganisation(Mockito.argThat(x -> assertOrganisationCreate(inputCreate, inputParent, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationUriPost() {
        var input = List.of(randomString(), randomString());

        var uriOrganisations = Set.of(randomOrganisationUriInner(), randomOrganisationUriInner(), randomOrganisationUriInner());
        Mockito.when(organisationByUriService.getOrganisationByUriWithDomain(input)).thenReturn(uriOrganisations);

        var result = organisationV2Controller.servicesV2OrganisationUriPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());

        assertEquals(3, result.getBody().size());
        assertTrue(result.getBody().containsAll(uriOrganisations));

        Mockito.verify(organisationByUriService).getOrganisationByUriWithDomain(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testServicesV2OrganisationUriPostEmptyList() {
        List<String> input = List.of();

        Set<OrganisationUriInner> uriOrganisations = Set.of();
        Mockito.when(organisationByUriService.getOrganisationByUriWithDomain(input)).thenReturn(uriOrganisations);

        var result = organisationV2Controller.servicesV2OrganisationUriPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());

        Mockito.verify(organisationByUriService).getOrganisationByUriWithDomain(input);
        verifyNoMoreInteractions();
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private OrganisationCreate randomOrganisationCreate() {
        return new OrganisationCreate()
                .code(randomString())
                .name(randomString());
    }

    private Organisation randomOrganisation() {
        var organisation = new Organisation();
        organisation.setOrganisationId(randomString());
        organisation.setOrganisationName(randomString());
        organisation.setPoolSize(321);
        organisation.setGroupId(234L);
        organisation.setSmsSenderName(randomString());
        organisation.setAllowCustomUriWithoutDomain(true);
        organisation.setSmsCallbackUrl(randomString());
        organisation.setHistoryApiKey(randomString());
        organisation.setDeviceWebhookEndpoint(randomString());
        organisation.setDeviceWebhookEndpointKey(randomString());

        return organisation;
    }

    private OrganisationUpdate randomOrganisationUpdate() {
        return new OrganisationUpdate()
                .poolSize(321)
                .smsSenderName(randomString())
                .allowCustomUriWithoutDomain(true)
                .smsCallbackUrl(randomString())
                .historyApiKey(randomString())
                .deviceWebhookEndpoint(randomString())
                .deviceWebhookEndpointKey(randomString());
    }

    private OrganisationUriInner randomOrganisationUriInner() {
        return new OrganisationUriInner()
                .code(randomString())
                .name(randomString())
                .groupId(123L)
                .uri(randomString())
                .booked(true)
                .groupName(randomString());
    }

    private void assertOrganisation(Organisation expected, OrganisationResponse actual) {
        assertEquals(expected.getGroupId(), actual.getGroupId(), 0);
        assertEquals(expected.getOrganisationId(), actual.getCode());
        assertEquals(expected.getOrganisationName(), actual.getName());
        assertEquals(expected.getPoolSize(), actual.getPoolSize());
        assertEquals(expected.getSmsSenderName(), actual.getSmsSenderName());
        assertEquals(expected.isAllowCustomUriWithoutDomain(), actual.getAllowCustomUriWithoutDomain());
        assertEquals(expected.getSmsCallbackUrl(), actual.getSmsCallbackUrl());
        assertEquals(expected.getHistoryApiKey(), actual.getHistoryApiKey());
        assertEquals(expected.getDeviceWebhookEndpoint(), actual.getDeviceWebhookEndpoint());
        assertEquals(expected.getDeviceWebhookEndpointKey(), actual.getDeviceWebhookEndpointKey());
    }

    private boolean assertOrganisationCreate(OrganisationCreate expected, String expectedParent, dk.medcom.vdx.organisation.service.model.OrganisationCreate actual) {
        assertEquals(expected.getCode(), actual.organisationCode());
        assertEquals(expected.getName(), actual.name());
        assertEquals(expectedParent, actual.parentOrganisation());
        if (expected.getPoolSize() == null) {
            assertNull(actual.poolSize());
        } else {
            assertEquals(expected.getPoolSize(), actual.poolSize(), 0);
        }
        assertEquals(expected.getSmsSenderName(), actual.smsSenderName());
        if (expected.getAllowCustomUriWithoutDomain() == null) {
            assertFalse(actual.allowCustomUriWithoutDomain());
        } else {
            assertEquals(expected.getAllowCustomUriWithoutDomain(), actual.allowCustomUriWithoutDomain());
        }
        assertEquals(expected.getSmsCallbackUrl(), actual.smsCallbackUrl());
        assertEquals(expected.getHistoryApiKey(), actual.historyApiKey());
        assertEquals(expected.getDeviceWebhookEndpoint(), actual.deviceWebhookEndpoint());
        assertEquals(expected.getDeviceWebhookEndpointKey(), actual.deviceWebhookEndpointKey());
        return true;
    }
}
