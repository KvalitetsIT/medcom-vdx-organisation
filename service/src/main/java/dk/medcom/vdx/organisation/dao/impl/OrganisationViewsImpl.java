package dk.medcom.vdx.organisation.dao.impl;

import dk.medcom.vdx.organisation.dao.OrganisationViews;
import dk.medcom.vdx.organisation.dao.entity.ViewGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Optional;

public class OrganisationViewsImpl implements OrganisationViews {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationViewsImpl.class);
    private final DataSource dataSource;

    public OrganisationViewsImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Long> getGroupIdFromLongLivedMeetingRooms(String uri) {
        if (uri == null || uri.isEmpty()) {
            return Optional.empty();
        }
        logger.debug("Enter getOrgFromLongLivedMeetingRooms");

        var template = new NamedParameterJdbcTemplate(dataSource);

        var parameters = new HashMap<String, Object>();
        parameters.put("uri_with_domain", uri.toLowerCase());

        String sql = "SELECT group_id FROM view_entities_meetingroom WHERE FIND_IN_SET(:uri_with_domain,LOWER(aliases))";

        try {
            Long result = template.queryForObject(sql, parameters, Long.class);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ViewGroups> getOrganisationFromViewGroup(Long groupId) {
        logger.debug("Enter getOrganisationName");
        var template = new NamedParameterJdbcTemplate(dataSource);

        var parameters = new HashMap<String, Object>();
        parameters.put("group_id", groupId);

        String sql = "SELECT group_name, organisation_id, organisation_id_name FROM view_groups WHERE group_id = :group_id";

        try {
            ViewGroups result = template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(ViewGroups.class));

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getGroupIdFromRegisteredClients(String uri) {
        if (uri == null || uri.isEmpty()) {
            return Optional.empty();
        }
        logger.debug("Enter getGroupIdFromRegisteredClients");

        var template = new NamedParameterJdbcTemplate(dataSource);

        var parameters = new HashMap<String, Object>();
        parameters.put("uri_with_domain", uri.toLowerCase());

        String sql = "SELECT relation_id FROM view_entities_registeredclients WHERE LOWER(alias) = :uri_with_domain";

        try {
            Long result = template.queryForObject(sql, parameters, Long.class);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getGroupIdFromDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            return Optional.empty();
        }
        logger.debug("Enter getGroupIdFromDomain");

        var template = new NamedParameterJdbcTemplate(dataSource);

        var parameters = new HashMap<String, Object>();
        parameters.put("domain", domain.toLowerCase());

        String sql = "SELECT group_id FROM groups_domains WHERE LOWER(domain) = :domain";

        try {
            Long result = template.queryForObject(sql, parameters, Long.class);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getGroupIdFromDomainLike(String domain) {
        if (domain == null || domain.isEmpty()) {
            return Optional.empty();
        }
        logger.debug("Enter getGroupIdFromDomain");

        var template = new NamedParameterJdbcTemplate(dataSource);

        var parameters = new HashMap<String, Object>();
        parameters.put("domain", domain.toLowerCase());

        String sql = "SELECT group_id FROM groups_domains WHERE LOWER(domain) LIKE '%:domain%'";

        try {
            Long result = template.queryForObject(sql, parameters, Long.class);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getGroupName(Long groupId) {
        logger.debug("Enter getOrganisationName");
        var template = new NamedParameterJdbcTemplate(dataSource);

        var parameters = new HashMap<String, Object>();
        parameters.put("group_id", groupId);

        String sql = "SELECT group_name FROM view_groups WHERE group_id = :group_id";

        try {
            String result = template.queryForObject(sql, parameters, String.class);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
