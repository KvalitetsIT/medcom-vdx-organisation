package dk.medcom.vdx.organisation.integrationtest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static GenericContainer<?> organisationService;
    private static String apiBasePath;
    private static String keycloakUrl;

    @AfterClass
    public static void afterClass() {
        if(organisationService != null) {
            organisationService.getDockerClient().stopContainerCmd(organisationService.getContainerId()).exec();
        }
    }

    @BeforeClass
    public static void beforeClass() {
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
}
