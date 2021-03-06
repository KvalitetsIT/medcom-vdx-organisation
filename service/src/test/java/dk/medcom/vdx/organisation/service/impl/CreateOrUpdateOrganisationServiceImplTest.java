package dk.medcom.vdx.organisation.service.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dk.medcom.vdx.organisation.api.OrganisationDto;
import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.exceptions.BadRequestException;
import dk.medcom.vdx.organisation.exceptions.DataIntegretyException;
import dk.medcom.vdx.organisation.exceptions.PermissionDeniedException;
import dk.medcom.vdx.organisation.exceptions.RessourceNotFoundException;
import dk.medcom.vdx.organisation.repository.RepositoryTest;
import dk.medcom.vdx.organisation.service.FindOrganisationService;

public class CreateOrUpdateOrganisationServiceImplTest extends RepositoryTest {

	static final String ORG_A_CODE = "org-a";
	static final String ORG_B_CODE = "org-b";
	static final String ORG_A_CODE_SUB = "sub-org-a";


	static final String ORG_PREVIOUSLY_DELETED = "deleted-2";

	UserContextService userWithNoOrganisationContext;
	UserContextService userFromOrgAContext;
	UserContextService userFromOrgBContext;

	@Autowired
	OrganisationDao organisationDao;

	CreateOrUpdateOrganisationServiceImpl subject;

	@Before
	public void setup() {

		userFromOrgAContext = new UserContextService() {
			@Override
			public boolean hasAnyNumberOfRoles(List<UserRole> allowed) {
				return true;
			}
			@Override
			public String getOrganisation() {
				return ORG_A_CODE;
			}
		};

		userWithNoOrganisationContext = new UserContextService() {
			@Override
			public boolean hasAnyNumberOfRoles(List<UserRole> allowed) {
				return true;
			}
			@Override
			public String getOrganisation() {
				return null;
			}
		};

		userFromOrgBContext = new UserContextService() {
			@Override
			public boolean hasAnyNumberOfRoles(List<UserRole> allowed) {
				return true;
			}
			@Override
			public String getOrganisation() {
				return ORG_B_CODE;
			}
		};
	}

	@Test
	public void testThatUserWithNoOrgACanCreateNewToplevelOrganisation() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		final String NEW_NAME = "Name";
		final String NEW_CODE = "123orgtest87654";
		final int NEW_POOL_SIZE = 100;
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toCreate = new OrganisationDto();
		toCreate.setCode(NEW_CODE);
		toCreate.setName(NEW_NAME);
		toCreate.setPoolSize(NEW_POOL_SIZE);

		// When
		Organisation newOrg = subject.createOrganisation(toCreate); 

		// Then
		Assert.assertNotNull(newOrg);
		Assert.assertEquals(NEW_CODE, newOrg.getOrganisationId());
		Assert.assertEquals(NEW_NAME, newOrg.getName());
		Assert.assertEquals(NEW_POOL_SIZE, newOrg.getPoolSize().intValue());
	}

	@Test(expected = DataIntegretyException.class)
	public void testThatDuplicateOrganisationCodesCannotBeCreated() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		final String NEW_NAME = "Name";
		final String NEW_CODE = ORG_A_CODE;
		final int NEW_POOL_SIZE = 100;
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toCreate = new OrganisationDto();
		toCreate.setCode(NEW_CODE);
		toCreate.setName(NEW_NAME);
		toCreate.setPoolSize(NEW_POOL_SIZE);

		// When
		subject.createOrganisation(toCreate); 
		
		// Then
	}

	@Test
	public void testThatUserWithNoOrgACanCreateNewToplevelOrganisationWithACodeThatWasPreviouslyDeleted() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		final String NEW_NAME = "Name";
		final String NEW_CODE = ORG_PREVIOUSLY_DELETED;
		final int NEW_POOL_SIZE = 100;
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toCreate = new OrganisationDto();
		toCreate.setCode(NEW_CODE);
		toCreate.setName(NEW_NAME);
		toCreate.setPoolSize(NEW_POOL_SIZE);

		// When
		Organisation newOrg = subject.createOrganisation(toCreate); 

		// Then
		Assert.assertNotNull(newOrg);
		Assert.assertEquals(NEW_CODE, newOrg.getOrganisationId());
		Assert.assertEquals(NEW_NAME, newOrg.getName());
		Assert.assertEquals(NEW_POOL_SIZE, newOrg.getPoolSize().intValue());
	}

	@Test
	public void testThatUserWithNoOrgCanMoveSubAToB() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode(ORG_A_CODE_SUB);
		toUpdate.setName("New name 123456789");
		toUpdate.setPoolSize(200);
		toUpdate.setParentCode(ORG_B_CODE);

		// When
		Organisation newOrgUnderB = subject.updateOrganisation(toUpdate); 

		// Then
		Assert.assertNotNull(newOrgUnderB);
		Assert.assertEquals(ORG_A_CODE_SUB, newOrgUnderB.getOrganisationId());
		Assert.assertEquals(toUpdate.getName(), newOrgUnderB.getName());
		Assert.assertEquals(toUpdate.getPoolSize(), newOrgUnderB.getPoolSize().intValue());
		Assert.assertEquals(ORG_B_CODE, newOrgUnderB.getParentOrganisationCode());
	}

	@Test(expected = DataIntegretyException.class)
	public void testOrgACannotBeMovedToItsOwnSubnodeSubA() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode(ORG_A_CODE);
		toUpdate.setName("New name 123456789");
		toUpdate.setPoolSize(200);
		toUpdate.setParentCode(ORG_A_CODE_SUB);

		// When
		subject.updateOrganisation(toUpdate);
		
		// Then
	}

	@Test
	public void testThatASubTreeCanBeMovedToBeANewRoot() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode("u21");
		toUpdate.setName("Not important");
		toUpdate.setPoolSize(200);
		toUpdate.setParentCode(null);

		// When
		Organisation updated = subject.updateOrganisation(toUpdate);
		
		// Then
		Assert.assertNotNull(updated);
		Assert.assertEquals(toUpdate.getCode(), updated.getOrganisationId());
		Assert.assertEquals(toUpdate.getName(), updated.getName());
		Assert.assertEquals(toUpdate.getPoolSize(), updated.getPoolSize().intValue());
		Assert.assertNull(updated.getParentOrganisationCode());
		Assert.assertNull(updated.getParentOrganisationId());
	}

	@Test
	public void testThatASubTreeCanBeMovedToAnotherOrg() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode("u21");
		toUpdate.setName("Not important");
		toUpdate.setPoolSize(200);
		toUpdate.setParentCode(ORG_A_CODE_SUB);

		// When
		Organisation updated = subject.updateOrganisation(toUpdate);
		
		// Then
		Assert.assertNotNull(updated);
		Assert.assertEquals(toUpdate.getCode(), updated.getOrganisationId());
		Assert.assertEquals(toUpdate.getName(), updated.getName());
		Assert.assertEquals(toUpdate.getPoolSize(), updated.getPoolSize().intValue());
		Assert.assertEquals(toUpdate.getParentCode(), updated.getParentOrganisationCode());
	}

	@Test
	public void testThatUserWithNoOrgCanUpdateA() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		final String NEW_NAME = "New fancy name xyz";
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode(ORG_A_CODE);
		toUpdate.setName(NEW_NAME);
		toUpdate.setPoolSize(200);

		// When
		Organisation newOrgA = subject.updateOrganisation(toUpdate); 

		// Then
		Assert.assertNotNull(newOrgA);
		Assert.assertEquals(ORG_A_CODE, newOrgA.getOrganisationId());
		Assert.assertEquals(NEW_NAME, newOrgA.getName());
		Assert.assertEquals(200, newOrgA.getPoolSize().intValue());
	}

	@Test
	public void testThatUserFromOrgACanUpdateA() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		final String NEW_NAME = "New fancy name";
		subject = new CreateOrUpdateOrganisationServiceImpl(userFromOrgAContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode(ORG_A_CODE);
		toUpdate.setName(NEW_NAME);
		toUpdate.setPoolSize(100);

		// When
		Organisation newOrgA = subject.updateOrganisation(toUpdate); 

		// Then
		Assert.assertNotNull(newOrgA);
		Assert.assertEquals(ORG_A_CODE, newOrgA.getOrganisationId());
		Assert.assertEquals(NEW_NAME, newOrgA.getName());
		Assert.assertEquals(100, newOrgA.getPoolSize().intValue());
		Assert.assertNull(newOrgA.getParentOrganisationCode());
	}

	@Test(expected = PermissionDeniedException.class)
	public void testThatUserFromOrgBCannotUpdateOrgA() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		final String NEW_NAME = "New fancy name";
		subject = new CreateOrUpdateOrganisationServiceImpl(userFromOrgBContext, organisationDao);
		OrganisationDto toUpdate = new OrganisationDto();
		toUpdate.setCode(ORG_A_CODE);
		toUpdate.setName(NEW_NAME);
		toUpdate.setPoolSize(100);

		// When
		subject.updateOrganisation(toUpdate);

		// Then
	}

	@Test(expected = PermissionDeniedException.class)
	public void testThatUserFromOrgBCannotCreateNewOrgUnderOrgA() throws Exception {

		// Given
		final String NEW_NAME = "New fancy name";
		subject = new CreateOrUpdateOrganisationServiceImpl(userFromOrgBContext, organisationDao);
		OrganisationDto toCreate = new OrganisationDto();
		toCreate.setCode("New suborg xyz");
		toCreate.setName(NEW_NAME);
		toCreate.setPoolSize(100);
		toCreate.setParentCode(ORG_A_CODE);

		// When
		subject.createOrganisation(toCreate);

		// Then
	}

	@Test
	public void testThatUserFromOrgBCanCreateNewOrgUnderOrgB() throws Exception {

		// Given
		final String NEW_NAME = "New fancy name";
		subject = new CreateOrUpdateOrganisationServiceImpl(userFromOrgBContext, organisationDao);
		OrganisationDto toCreate = new OrganisationDto();
		toCreate.setCode("New suborg xyz");
		toCreate.setName(NEW_NAME);
		toCreate.setPoolSize(117);
		toCreate.setParentCode(ORG_B_CODE);

		// When
		Organisation newOrg = subject.createOrganisation(toCreate);

		// Then
		Assert.assertNotNull(newOrg);
		Assert.assertEquals(toCreate.getCode(), newOrg.getOrganisationId());
		Assert.assertEquals(toCreate.getName(), newOrg.getName());
		Assert.assertEquals(toCreate.getPoolSize(), newOrg.getPoolSize().intValue());
		Assert.assertEquals(ORG_B_CODE, newOrg.getParentOrganisationCode());
	}

	@Test
	public void testThatUserFromNoOrgCanCreateNewOrgUnderOrgB() throws Exception {

		// Given
		final String NEW_NAME = "New fancy name";
		subject = new CreateOrUpdateOrganisationServiceImpl(userWithNoOrganisationContext, organisationDao);
		OrganisationDto toCreate = new OrganisationDto();
		toCreate.setCode("New 12388");
		toCreate.setName(NEW_NAME);
		toCreate.setPoolSize(119);
		toCreate.setParentCode(ORG_B_CODE);

		// When
		Organisation newOrg = subject.createOrganisation(toCreate);

		// Then
		Assert.assertNotNull(newOrg);
		Assert.assertEquals(toCreate.getCode(), newOrg.getOrganisationId());
		Assert.assertEquals(toCreate.getName(), newOrg.getName());
		Assert.assertEquals(toCreate.getPoolSize(), newOrg.getPoolSize().intValue());
		Assert.assertEquals(ORG_B_CODE, newOrg.getParentOrganisationCode());
	}
	
	@Test
	public void testThatUserFromOrgBCanDeleteOrgB() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException {

		// Given
		subject = new CreateOrUpdateOrganisationServiceImpl(userFromOrgBContext, organisationDao);
		FindOrganisationService find = new FindOrganisationServiceImpl(userFromOrgBContext, organisationDao);

		// When
		subject.deleteOrganisationWithCode(ORG_B_CODE);
		List<Organisation> organisations = find.findOrganisations();

		// Then
		Assert.assertNotNull(organisations);
		Assert.assertEquals(0, organisations.size());
	}
	
	@Test
	public void testThatUserFromOrgACanDeleteTheSubOrgForA() throws PermissionDeniedException, RessourceNotFoundException, BadRequestException, DataIntegretyException {

		// Given
		subject = new CreateOrUpdateOrganisationServiceImpl(userFromOrgAContext, organisationDao);
		FindOrganisationService find = new FindOrganisationServiceImpl(userFromOrgAContext, organisationDao);

		// When
		subject.deleteOrganisationWithCode(ORG_A_CODE_SUB);
		List<Organisation> organisations = find.findOrganisations();

		// Then
		Assert.assertNotNull(organisations);
		Assert.assertEquals(1, organisations.size());
	}

}
