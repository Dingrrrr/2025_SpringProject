package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dita.domain.Inv_log;

public interface InvLogRepository extends JpaRepository<Inv_log, Integer> {

	@Query(value = "SELECT d.drug_id, d.drug_name, d.form_type, d.drug_code, MAX(l.location) AS location, SUM(CASE WHEN l.change_type = 'IN' THEN l.quantity WHEN l.change_type = 'OUT' THEN -l.quantity ELSE 0 END) AS stock FROM inv_log l JOIN drug d ON l.drug_id = d.drug_id GROUP BY d.drug_id, d.drug_name, d.form_type, d.drug_code", nativeQuery = true)
	List<Object[]> findAllCurrentInventory();
	
	
	@Query("SELECT COALESCE(SUM(CASE WHEN l.changeType = 'IN' THEN l.quantity WHEN l.changeType = 'OUT' THEN -l.quantity ELSE 0 END), 0) FROM Inv_log l WHERE l.drug.drugId = :drugId")
	int calculateCurrentStock(@Param("drugId") int drugId);
	
	List<Inv_log> findByDrug_DrugIdOrderByOccurredAtDesc(int drugId);

}
