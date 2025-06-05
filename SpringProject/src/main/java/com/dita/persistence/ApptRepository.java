
package com.dita.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dita.domain.Appt;
import com.dita.domain.Patient;

public interface ApptRepository extends JpaRepository<Appt, Integer> {
	List<Appt> findByStatus(com.dita.domain.Appt status);
	List<Appt> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
	Optional<Appt> findByScheduledAtAndPatient_PatientBirth(LocalDateTime date, String patientBirth);
	

}