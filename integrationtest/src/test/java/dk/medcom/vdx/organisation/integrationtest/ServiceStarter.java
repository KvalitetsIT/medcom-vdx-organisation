package dk.medcom.vdx.organisation.integrationtest;

import dk.medcom.vdx.organisation.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ServiceStarter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger serviceLogger = LoggerFactory.getLogger("medcom-vdx-organisation");
    private static final Logger mariadbLogger = LoggerFactory.getLogger("mariadb");

    private Network dockerNetwork;
    private String jdbcUrl;
    private static boolean firstStart = true;

    public void startServices() {
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();

        System.setProperty("JDBC.URL", jdbcUrl);
        System.setProperty("JDBC.USER", "hellouser");
        System.setProperty("JDBC.PASS", "secret1234");

        System.setProperty("sessiondata_headername", "x-sessiondata");

        System.setProperty("userservice_token_attribute_organisation", "dk:medcom:organisation_id");
        System.setProperty("userservice_token_attribute_email", "dk:medcom:email");
        System.setProperty("userservice_token_attribute_userrole", "dk:medcom:video:role");

        System.setProperty("mapping_role_provisioner", "meeting-provision");
        System.setProperty("mapping_role_user", "meeting-user");
        System.setProperty("mapping_role_meeting_planner", "meeting-planner");
        System.setProperty("mapping_role_admin", "meeting-admin");

        System.setProperty("ALLOWED_ORIGINS", "*");

        SpringApplication.run(Application.class);
    }

    public GenericContainer<?> startServicesInDocker() {
        if(firstStart) {
            firstStart = false;

            dockerNetwork = Network.newNetwork();

            setupDatabaseContainer();
        }

        GenericContainer<?> service;

        // Start service
        service = new GenericContainer<>("local/medcom-vdx-organisation-qa:dev")
                .withFileSystemBind("/tmp", "/jacoco-output", BindMode.READ_WRITE)
                .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-output/jacoco-it.exec,dumponexit=true -cp integrationtest.jar");

        service.withNetwork(dockerNetwork)
                .withNetworkAliases("medcom-vdx-organisation")

                .withEnv("LOG_LEVEL", "INFO")

                .withEnv("JDBC_URL", "jdbc:mariadb://mariadb:3306/hellodb")
                .withEnv("JDBC_USER", "hellouser")
                .withEnv("JDBC_PASS", "secret1234")

                .withEnv("sessiondata_headername", "x-sessiondata")

                .withEnv("userservice_token_attribute_organisation", "dk:medcom:organisation_id")
                .withEnv("userservice_token_attribute_email", "dk:medcom:email")
                .withEnv("userservice_token_attribute_userrole", "dk:medcom:video:role")

                .withEnv("mapping_role_provisioner", "meeting-provision")
                .withEnv("mapping_role_user", "meeting-user")
                .withEnv("mapping_role_meeting_planner", "meeting-planner")
                .withEnv("mapping_role_admin", "meeting-admin")

                .withEnv("ALLOWED_ORIGINS", "*")

                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")

                .withClasspathResourceMapping("db/migration/V903__insert_test_data.sql", "/app/sql/V903__insert_test_data.sql", BindMode.READ_ONLY)
                .withClasspathResourceMapping("db/migration/V902__views.sql", "/app/sql/V902__views.sql", BindMode.READ_ONLY)
//                .withEnv("JVM_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000")

                .withExposedPorts(8081,8080)
                .waitingFor(Wait.forHttp("/actuator").forPort(8081).forStatusCode(200));
        service.start();
        attachLogger(serviceLogger, service);

        return service;
    }

    private void setupDatabaseContainer() {
        // Database server
        var mariadb = new MariaDBContainer<>("mariadb:10.6")
                .withDatabaseName("hellodb")
                .withUsername("hellouser")
                .withPassword("secret1234")
                .withNetwork(dockerNetwork)
                .withNetworkAliases("mariadb");
        mariadb.start();
        jdbcUrl = mariadb.getJdbcUrl();
        attachLogger(mariadbLogger, mariadb);
    }

    private void attachLogger(Logger logger, GenericContainer<?> container) {
        ServiceStarter.logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }
}
