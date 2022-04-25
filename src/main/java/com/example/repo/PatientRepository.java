package com.example.repo;

import com.example.models.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Patient findByEmail(String email);
    List<Patient> findByOrderByIdAsc();
    List<Patient> findByNameIgnoreCaseOrSurnameIgnoreCase(String name, String surname);
    Patient findByActivationCode(String activationCode);

    Page<Patient> findAll(Pageable pageable);

    @Query(value = "Select * from patient\n" +
            "where LOWER(CONCAT(' ', name, ' ', surname, ' ')) like LOWER(CONCAT('% ', :name, ' %'))", nativeQuery = true)
    Page<Patient> findPatientByOneString(@Param("name") String name, Pageable pageable);

    @Query(value = """
            Select * from patient
            where LOWER(CONCAT(' ', name, ' ', surname, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))""",
            countQuery = """
            Select count(*) from patient
            where LOWER(CONCAT(' ', name, ' ', surname, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))""",
            nativeQuery = true)
    Page<Patient> findPatientByTwoStrings(@Param("name") String name, @Param("surname") String surname, Pageable pageable);

    boolean existsByEmail(String email);
}
