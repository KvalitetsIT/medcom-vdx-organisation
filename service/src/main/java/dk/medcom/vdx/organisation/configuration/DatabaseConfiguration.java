package dk.medcom.vdx.organisation.configuration;

import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.impl.GroupsDaoImpl;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableJpaRepositories( basePackages = "dk.medcom.vdx.organisation")
@EntityScan(basePackages = { "dk.medcom.vdx.organisation" })
@EnableTransactionManagement
public class DatabaseConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Bean
    public GroupsDao groupsDao(DataSource dataSource) {
        return new GroupsDaoImpl(dataSource);
    }

    @Bean
    public DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl, @Value("${jdbc.user}") String jdbcUser, @Value("${jdbc.pass}") String jdbcPass) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUser);
        dataSource.setPassword(jdbcPass);

        return dataSource;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource, @Value("${spring.flyway.locations:classpath:db/migration}") List<String> locations, @Value("${FLYWAY_BASELINE_ON_MIGRATE:false}") boolean baseline) {
        logger.info("Reading migration files from {}.", locations);
        return Flyway.configure()
                .dataSource(dataSource)
                .table("organisation_flyway_schema_history")
                .locations(locations.toArray(String[]::new))
                .load();
    }
}