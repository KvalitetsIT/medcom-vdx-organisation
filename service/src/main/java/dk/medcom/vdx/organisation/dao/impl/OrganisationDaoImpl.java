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
                "o.allow_custom_uri_without_domain, " +
                "o.sms_callback_url, " +
                "o.history_api_key, " +
                "o.device_webhook_endpoint, " +
                "o.device_webhook_endpoint_key " +
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
                "o.sms_callback_url, " +
                "o.device_webhook_endpoint, " +
                "o.device_webhook_endpoint_key " +
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
                "o.sms_callback_url, " +
                "o.device_webhook_endpoint, " +
                "o.device_webhook_endpoint_key " +
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
        var sql = "insert into organisation(group_id, organisation_id, name, pool_size, sms_sender_name, allow_custom_uri_without_domain, sms_callback_url, history_api_key, device_webhook_endpoint, device_webhook_endpoint_key)" +
                " values(:group_id, :organisation_id, :name, :pool_size, :sms_sender_name, :allow_custom_uri_with_domain, :sms_callback_url, :history_api_key, :device_webhook_endpoint, :device_webhook_endpoint_key)";

        var parameters = new MapSqlParameterSource().
                addValue("group_id", newOrganisation.getGroupId()).
                addValue("organisation_id", newOrganisation.getOrganisationId()).
                addValue("name", newOrganisation.getOrganisationName()).
                addValue("pool_size", newOrganisation.getPoolSize()).
                addValue("sms_sender_name", newOrganisation.getSmsSenderName()).
                addValue("allow_custom_uri_with_domain", newOrganisation.isAllowCustomUriWithoutDomain()).
                addValue("sms_callback_url", newOrganisation.getSmsCallbackUrl()).
                addValue("history_api_key", newOrganisation.getHistoryApiKey()).
                addValue("device_webhook_endpoint", newOrganisation.getDeviceWebhookEndpoint()).
                addValue("device_webhook_endpoint_key", newOrganisation.getDeviceWebhookEndpointKey());

        var keyHolder = new GeneratedKeyHolder();

        template.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(Organisation organisation) {
        logger.debug("Updating organisation.");

        var sql = "update organisation set " +
                "       pool_size = :pool_size, " +
                "       sms_sender_name = :sms_sender_name, " +
                "       allow_custom_uri_without_domain = :allow_custom_uri_without_domain, " +
                "       sms_callback_url = :sms_callback_url, " +
                "       history_api_key = :history_api_key, " +
                "       device_webhook_endpoint = :device_webhook_endpoint, " +
                "       device_webhook_endpoint_key = :device_webhook_endpoint_key " +
                "   where organisation_id = :organisation_id";

        var parameters = new MapSqlParameterSource()
                .addValue("organisation_id", organisation.getOrganisationId())
                .addValue("pool_size", organisation.getPoolSize())
                .addValue("sms_sender_name", organisation.getSmsSenderName())
                .addValue("allow_custom_uri_without_domain", organisation.isAllowCustomUriWithoutDomain())
                .addValue("sms_callback_url", organisation.getSmsCallbackUrl())
                .addValue("history_api_key", organisation.getHistoryApiKey())
                .addValue("device_webhook_endpoint", organisation.getDeviceWebhookEndpoint())
                .addValue("device_webhook_endpoint_key", organisation.getDeviceWebhookEndpointKey());

        return template.update(sql, parameters) > 0;
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
                "o.sms_callback_url, " +
                "o.device_webhook_endpoint, " +
                "o.device_webhook_endpoint_key " +
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
