package dk.medcom.vdx.organisation.dao.impl;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrganisationDaoImpl implements OrganisationDao {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationDaoImpl.class);
    private final DataSource dataSource;
    private final NamedParameterJdbcTemplate template;

    public OrganisationDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Organisation findOrganisation(String code) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name, " +
                "o.sms_sender_name, " +
                "o.sms_callback_url " +
                "from organisation o, groups g " +
                "where o.organisation_id = :organisation_id" +
                "  and g.group_id = o.group_id";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("organisation_id", code);

        try {
            return template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class));
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Optional<Organisation> findOrganisationByGroupId(long groupId) {
        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name, " +
                "o.sms_sender_name, " +
                "o.sms_callback_url " +
                "from groups g left outer join (select * from organisation where deleted_time = '0001-01-01 00:00:00') o on g.group_id = o.group_id " +
                "where g.group_id = :group_id " +
                "and g.deleted_time = '0001-01-01 00:00:00'";

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("group_id", groupId);

        try {
            return Optional.ofNullable(template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Group not found: {}", groupId);
            return Optional.empty();
        }
    }

    @Override
    public List<Organisation> findOrganisationByParentId(long groupId) {
        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name, " +
                "o.sms_sender_name, " +
                "o.sms_callback_url " +
                "from groups g left outer join (select * from organisation where deleted_time = '0001-01-01 00:00:00') o on g.group_id = o.group_id " +
                "where g.parent_id = :group_id " +
                "and g.deleted_time = '0001-01-01 00:00:00'";

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("group_id", groupId);

        return template.query(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class));
    }

    @Override
    public long insert(Organisation newOrganisation) {
        var sql = "insert into organisation(group_id, organisation_id, name, pool_size, sms_sender_name, allow_custom_uri_without_domain, sms_callback_url)" +
                " values(:group_id, :organisation_id, :name, :pool_size, :sms_sender_name, :allow_custom_uri_with_domain, :sms_callback_url)";

        var parameters = new MapSqlParameterSource().
                addValue("group_id", newOrganisation.getGroupId()).
                addValue("organisation_id", newOrganisation.getOrganisationId()).
                addValue("name", newOrganisation.getOrganisationName()).
                addValue("pool_size", newOrganisation.getPoolSize()).
                addValue("sms_sender_name", newOrganisation.getSmsSenderName()).
                addValue("allow_custom_uri_with_domain", 0).
                addValue("sms_callback_url", newOrganisation.getSmsCallbackUrl());

        var keyHolder = new GeneratedKeyHolder();

        template.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Organisation findOrganisationByHistoryApiKey(String historyApiKey) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name, " +
                "o.sms_sender_name, " +
                "o.sms_callback_url " +
                "from organisation o, groups g " +
                "where o.history_api_key = :history_api_key" +
                "  and g.group_id = o.group_id";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("history_api_key", historyApiKey);

        try {
            return template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class));
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }
}
