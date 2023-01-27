package dk.medcom.vdx.organisation.dao.jpa;


import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrganisationRepository extends CrudRepository<Organisation, Long> {
	
	Organisation findByOrganisationId(String organisationId);

	List<Organisation> findByPoolSizeNotNull();
}
