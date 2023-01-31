package dk.medcom.vdx.organisation.dao.impl;

import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.entity.Groups;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;

public class GroupsDaoImpl implements GroupsDao {
    private final NamedParameterJdbcTemplate template;

    public GroupsDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(Groups group) {
        var sql = "insert into groups(group_name, group_type, parent_id, created_by) " +
                "      values(:group_name, :group_type, :parent_id, :created_by)";

        var keyHolder = new GeneratedKeyHolder();

        var parameterMap = new MapSqlParameterSource().
                addValue("group_name", group.groupName()).
                addValue("group_type", group.groupType()).
                addValue("parent_id", group.parentId()).
                addValue("created_by", group.createdBy());

        template.update(sql, parameterMap, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
