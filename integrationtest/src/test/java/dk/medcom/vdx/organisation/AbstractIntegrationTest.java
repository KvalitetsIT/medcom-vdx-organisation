package dk.medcom.vdx.organisation;

import com.github.dockerjava.api.model.VolumesFrom;
import org.openapitools.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.util.Base64;
import java.util.Collections;

public class AbstractIntegrationTest {
	private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);
	private static final Logger mysqlLogger = LoggerFactory.getLogger("mysql");
	private static final Logger organisationLogger = LoggerFactory.getLogger("organisation");

	public static final String TEST_AUTH_HEADER = "X-Test-Auth";
	public static final String TEST_USER_ATTRIBUTES_ROLE_KEY = "UserRoles";
	public static final String TEST_USER_ATTRIBUTES_ORG_KEY = "Org";
	public static final String TEST_ROLE_ADMIN = "admin";
	public static final String TEST_ROLE_USER_1 = "justuser";
	public static final String TEST_ROLE_USER_2 = "user";
	public static final String TEST_ROLE_MONITOR = "monitor";
	public static final String TEST_ROLE_PROVISIONER = "provisioner";
	
	public static final String TEST_ORGANISATION_A = "org-a";
	public static final String TEST_ORGANISATION_A_SUB = "sub-org-a";
	
	public static final String TEST_ORGANISATION_B = "org-b";
	
	
	private static Network dockerNetwork;
	
	private static String apiBasePath;
	private static String actuatorPrometheusBasePath;
	private static GenericContainer organisationService;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
			if(organisationService != null) {
				logger.info("Stopping organisation container: " + organisationService.getContainerId());
				organisationService.getDockerClient().stopContainerCmd(organisationService.getContainerId()).exec();
			}
			}
		});

		setup();
	}

	public static void setup() {
		if (dockerNetwork == null) {
			dockerNetwork = Network.newNetwork();

			var mysql = setupDatabaseContainer();

			setupOrganisationService(mysql);
		}
	}

	private static MySQLContainer setupDatabaseContainer() {
		// Database server for Organisation.
		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.7")
				.withDatabaseName("organisationdb")
				.withUsername("orguser")
				.withPassword("secret1234")
				.withNetwork(dockerNetwork)
				.withNetworkAliases("mysql");
		mysql.start();
		attachLogger(mysql, mysqlLogger);

		return mysql;
	}

	private static void setupOrganisationService(MySQLContainer mysql) {
		var runInDocker = Boolean.getBoolean("runInDocker");
		logger.info("Running integration test in docker container: " + runInDocker);

		var resourcesContainerName = "medcom-vdx-organisation-resources";
		var resourcesRunning = containerRunning(resourcesContainerName);
		logger.info("Resource container is running: " + resourcesRunning);

		if(runInDocker) {
			// OrganisationsAPI
			if(resourcesRunning) {
				VolumesFrom volumesFrom = new VolumesFrom(resourcesContainerName);
				organisationService = new GenericContainer<>("local/medcom-vdx-organisation-qa:dev")
						.withCreateContainerCmdModifier(modifier -> modifier.withVolumesFrom(volumesFrom))
						.withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true,append=true -cp integrationtest.jar");
			}
			else {
				organisationService = new GenericContainer<>("local/medcom-vdx-organisation-qa:dev")
						.withFileSystemBind("/tmp", "/jacoco-report/")
						.withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true -cp integrationtest.jar");
			}

			organisationService.withNetwork(dockerNetwork)
					.withNetworkAliases("organisation")
					.withEnv("jdbc_url", "jdbc:mysql://mysql:3306/organisationdb")
					.withEnv("jdbc_user", "orguser")
					.withEnv("jdbc_pass", "secret1234")

					.withEnv("usercontext_header_name", TEST_AUTH_HEADER)
					.withEnv("userattributes_role_key", TEST_USER_ATTRIBUTES_ROLE_KEY)
					.withEnv("userrole_admin_values", TEST_ROLE_ADMIN)
					.withEnv("userrole_user_values", TEST_ROLE_USER_1+","+TEST_ROLE_USER_2)
					.withEnv("userrole_monitor_values", TEST_ROLE_MONITOR)
					.withEnv("userrole_provisioner_values", TEST_ROLE_PROVISIONER)
					.withEnv("userattributes_org_key", TEST_USER_ATTRIBUTES_ORG_KEY)

					// Contains integrationtestdata
					.withEnv("LOADER_PATH", "/app/lib")

					.withEnv("LOG_LEVEL", "DEBUG")

					.withExposedPorts(8080, 8081)
					.waitingFor(Wait.forHttp("/temp").forStatusCode(404)); //TODO: Bruge info-url
			organisationService.start();
			attachLogger(organisationService, organisationLogger);

			apiBasePath = "http://"+organisationService.getContainerIpAddress()+":"+organisationService.getMappedPort(8080);
			actuatorPrometheusBasePath = "http://"+organisationService.getContainerIpAddress()+":"+organisationService.getMappedPort(8081);

		}
		else {
			String jdbcUrl = mysql.getJdbcUrl();
			System.setProperty("jdbc.url", jdbcUrl);
			SpringApplication.run(Application.class);
			apiBasePath = "http://localhost:8080";
			actuatorPrometheusBasePath = "http://localhost:8081";
		}
	}

	private static boolean containerRunning(String containerName) {
		return DockerClientFactory
				.instance()
				.client()
				.listContainersCmd()
				.withNameFilter(Collections.singleton(containerName))
				.exec()
				.size() != 0;
	}

	private static void attachLogger(GenericContainer container, Logger logger) {
		logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
		container.followOutput(logConsumer);
	}

	protected String getApiBasePath() {
		return apiBasePath;
	}

	protected String getActuatorPrometheusBasePath(){
		return actuatorPrometheusBasePath;
	}

	protected void setUserContext(ApiClient apiClient, String[] roles) {
		setUserContext(apiClient, roles, null);
	}

	protected String getEncodedUserContext(String[] roles, String org) {
		StringBuffer sessionData = new StringBuffer();
		sessionData.append("{");
		sessionData.append("\"UserAttributes\": { ");
		boolean firstAttr = true;
		if (roles != null && roles.length > 0) {
			firstAttr = false;
			sessionData.append("\""+TEST_USER_ATTRIBUTES_ROLE_KEY+"\": [");
			boolean firstRole = true;
			for (String role : roles) {
				sessionData.append("\""+role+"\"");
				if (!firstRole) {
					sessionData.append(",");
				}
				firstRole = false;
			}
			sessionData.append("]");
		}
		if (org != null) {
			if (!firstAttr) {
				sessionData.append(",");
			}
			sessionData.append("\""+TEST_USER_ATTRIBUTES_ORG_KEY+"\": [\""+org+"\"]");
			firstAttr = false;
		}
		sessionData.append("}");
		sessionData.append("}");
		byte[] sessionDataBytes = sessionData.toString().getBytes();
		return Base64.getEncoder().encodeToString(sessionDataBytes);
	}

	protected void setUserContext(ApiClient apiClient, String[] roles, String org) {
		apiClient.addDefaultHeader(TEST_AUTH_HEADER, getEncodedUserContext(roles, org));
	}
}