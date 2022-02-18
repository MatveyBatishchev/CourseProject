package com.example.repo;

import com.example.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);
    List<Patient> findByNameIgnoreCaseOrSurnameIgnoreCase(String name, String surname);
}
