package dk.medcom.vdx.organisation.configuration;

import dk.medcom.vdx.organisation.context.UserContextFactory;
import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.context.UserContextServiceImpl;
import dk.medcom.vdx.organisation.context.WspUserContext;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.OrganisationViews;
import dk.medcom.vdx.organisation.dao.impl.OrganisationDaoImpl;
import dk.medcom.vdx.organisation.dao.impl.OrganisationViewsImpl;
import dk.medcom.vdx.organisation.interceptor.UserSecurityInterceptor;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableAspectJAutoProxy
public class OrganisationConfiguration implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationConfiguration.class);

    @Value("${ALLOWED_ORIGINS}")
    private List<String> allowedOrigins;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.debug("Adding interceptors");
        registry.addInterceptor(userSecurityInterceptor());
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        allowedOrigins.forEach(config::addAllowedOrigin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);

        return bean;
    }

    @Bean
    public UserSecurityInterceptor userSecurityInterceptor() {
        logger.debug("Creating userSecurityInterceptor");
        return new UserSecurityInterceptor();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
    public UserContextService userContextService(UserContextFactory userContextFactory) {
        return new UserContextServiceImpl(userContextFactory);
    }

    @Bean
    public UserContextFactory userContextFactory(@Value("${sessiondata_headername}") String sessionDataHttpHeaderInput,
                                                 @Value("${userservice_token_attribute_organisation}") String userServiceTokenAttributeOrganisation,
                                                 @Value("${userservice_token_attribute_email}") String userServiceTokenAttributeEmail,
                                                 @Value("${userservice_token_attribute_userrole}") String userServiceTokenAttributeUserRole,
                                                 @Value("${mapping_role_provisioner}") String mappingRoleProvisioner,
                                                 @Value("${mapping_role_admin}") String mappingRoleAdmin,
                                                 @Value("${mapping_role_user}") String mappingRoleUser,
                                                 @Value("${mapping_role_meeting_planner}") String mappingRoleMeetingPlanner) {
        return new WspUserContext(sessionDataHttpHeaderInput, userServiceTokenAttributeOrganisation, userServiceTokenAttributeEmail, userServiceTokenAttributeUserRole, mappingRoleProvisioner, mappingRoleAdmin, mappingRoleUser, mappingRoleMeetingPlanner);
    }

    @Bean
    public OrganisationTreeService organisationTreeService(OrganisationDao organisationDao) {
        return new OrganisationTreeServiceImpl(organisationDao);
    }

    @Bean
    public OrganisationTreeBuilder organisationTreeBuilder() {
        return new OrganisationTreeBuilderImpl();
    }

    @Bean
    public OrganisationDao organisationDao(DataSource dataSource) {
        return new OrganisationDaoImpl(dataSource);
    }

    @Bean
    public OrganisationViews organisationViews(DataSource dataSource) { return new OrganisationViewsImpl(dataSource); }
}
