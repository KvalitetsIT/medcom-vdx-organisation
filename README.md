![Build Status](https://github.com/KvalitetsIT/medcom-vdx-organisation/workflows/CICD/badge.svg)
# medcom-vdx-organisation

## Endpoints

### Service

The service is listening for connections on port 8080.

Spring boot actuator is listening for connections on port 8081. This is used as prometheus scrape endpoint and health monitoring. 

Prometheus scrape endpoint: `http://localhost:8081/actuator/prometheus`  
Health URL that can be used for readiness probe: `http://localhost:8081/actuator/health`

### Documentation

Documentation of the API is build as a separate Docker image. Documentation is build using Swagger. The documentation 
image is post-fixed with `-documentation`. The file `documentation/docker/compose/docker-compose.yml` contains a  setup 
that starts both the service and documentation image. The documentation can be accessed at `http://localhost/test` 
and the service can be called through the Swagger UI. 

In the docker-compose setup is also an example on how to set custom endpoints for the Swagger documentation service.

## Configuration

| Environment variable                     | Description                                                                                          | Required |
|------------------------------------------|------------------------------------------------------------------------------------------------------|----------|
| JDBC_URL                                 | JDBC connection URL                                                                                  | Yes      |
| JDBC_USER                                | JDBC user                                                                                            | Yes      |
| JDBC_PASS                                | JDBC password                                                                                        | Yes      |
| LOG_LEVEL                                | Log Level for applikation  log. Defaults to INFO.                                                    | No       |
| LOG_LEVEL_FRAMEWORK                      | Log level for framework. Defaults to INFO.                                                           | No       |
| CORRELATION_ID                           | HTTP header to take correlation id from. Used to correlate log messages. Defaults to "x-request-id". | No       |
| sessiondata_headername                   | HTTP header containing session data.                                                                 | Yes      |
| userservice_token_attribute_organisation | Attribute to read organisation from in sessiondata_header                                            | Yes      | 
| userservice_token_attribute_email        | Attribute to read email from in sessiondata_header                                                   | Yes      |
| userservice_token_attribute_userrole     | Attribute to read user role from in sessiondata_header                                               | Yes      | 
| mapping_role_provisioner                 | Attribute value to map to meeting-provision role.                                                    | Yes      |
| mapping_role_user                        | Attribute value to map to meeting-user role.                                                         | Yes      |
| mapping_role_meeting_planner             | Attribute value to map to meeting-planner role.                                                      | Yes      |
| mapping_role_admin                       | Attribute value to map to meeting-admin role.                                                        | Yes      |
 | ALLOWED_ORIGINS                          | Allowed CORS origins                                                                                 | Yes      |  
