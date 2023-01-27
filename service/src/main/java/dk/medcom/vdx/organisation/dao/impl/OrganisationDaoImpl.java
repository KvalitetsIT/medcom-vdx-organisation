package dk.medcom.vdx.organisation.dao.impl;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class OrganisationDaoImpl implements OrganisationDao {
    private final DataSource dataSource;

    public OrganisationDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
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
    public Organisation findOrganisationByGroupId(long groupId) {
        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name, " +
                "o.sms_sender_name, " +
                "o.sms_callback_url " +
                "from groups g left outer join organisation o on g.group_id = o.group_id " +
                "where g.group_id = :group_id " +
                "and g.deleted_time = '0001-01-01 00:00:00'";

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("group_id", groupId);

        return template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class));
    }
}
