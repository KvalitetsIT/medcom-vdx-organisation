package dk.medcom.vdx.organisation.integrationtest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.function.Executable;
import org.openapitools.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static GenericContainer<?> organisationService;
    private static String apiBasePath;
    private static String keycloakUrl;

    @AfterAll
    static void afterAll() {
        if(organisationService != null) {
            organisationService.getDockerClient().stopContainerCmd(organisationService.getContainerId()).exec();
        }
    }

    @BeforeAll
    static void beforeAll() {
        setup();
    }

    private static void setup() {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: {}", runInDocker);

        ServiceStarter serviceStarter;
        serviceStarter = new ServiceStarter();
        if(runInDocker) {
            organisationService = serviceStarter.startServicesInDocker();
            apiBasePath = "http://" + organisationService.getHost() + ":" + organisationService.getMappedPort(8080);
        } else if (serviceStarter.isFirstStart()) {
            serviceStarter.startServices();
            apiBasePath = "http://localhost:8080";
        }
        keycloakUrl = serviceStarter.getKeycloakUrl();
    }

    protected String getApiBasePath() {
        return apiBasePath;
    }

    protected String getKeycloakUrl() {
        return keycloakUrl;
    }

    protected static void assertThrowsWithStatus(int expectedCode, Executable call) {
        var ex = assertThrows(ApiException.class, call);
        assertEquals(expectedCode, ex.getCode(), String.format("Expected status code: %s", expectedCode));
    }
}
