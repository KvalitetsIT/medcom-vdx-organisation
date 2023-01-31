package dk.medcom.vdx.organisation.dao.impl;

import dk.medcom.vdx.organisation.dao.OrganisationTemplateDao;
import dk.medcom.vdx.organisation.dao.entity.OrganisationTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;

public class OrganisationTemplateDaoImpl implements OrganisationTemplateDao {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationTemplateDaoImpl.class);

    private NamedParameterJdbcTemplate template;

    public OrganisationTemplateDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }


    @Override
    public Optional<OrganisationTemplate> findOrganisationTemplate(String organisationCode) {
        var sql = "select * from organisation_template where organisation_code = :organisation_code";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("organisation_code", organisationCode), DataClassRowMapper.newInstance(OrganisationTemplate.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Organisation template not found.");

            return Optional.empty();
        }
    }
}
