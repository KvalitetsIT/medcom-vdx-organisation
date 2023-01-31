package dk.medcom.vdx.organisation.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class OrganisationTemplateDaoTest extends AbstractDaoTest {
    @Autowired
    private OrganisationTemplateDao organisationTemplateDao;

    @Test
    public void testFindOrganisationNotFound() {
        var result = organisationTemplateDao.findOrganisationTemplate("not_found");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindOrganisationFound() {
        var result = organisationTemplateDao.findOrganisationTemplate("adfs");

        assertNotNull(result);
        assertTrue(result.isPresent());

        var organisationTemplate = result.get();
        assertEquals("adfs", organisationTemplate.organisationCode());
        assertEquals("This is ADFS", organisationTemplate.name());
        assertEquals("test-org", organisationTemplate.parentOrganisationCode());
    }
}
