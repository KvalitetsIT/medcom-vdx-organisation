package dk.medcom.vdx.organisation.dao.jpa;

import dk.medcom.vdx.organisation.dao.jpa.entity.ProvisionStatus;
import dk.medcom.vdx.organisation.dao.jpa.entity.SchedulingInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SchedulingInfoRepository extends CrudRepository<SchedulingInfo, Long> {
	@Query("SELECT s FROM SchedulingInfo s WHERE s.provisionStatus = ?2 AND s.uriWithDomain IN (?1)")
	List<SchedulingInfo> findAllByUriWithDomainAndProvisionStatusOk(List<String> uri, ProvisionStatus provisionStatus);
}
