package com.example.repo;

import com.example.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByOrderByIdAsc();
    List<Doctor> findByNameIgnoreCaseOrSurnameIgnoreCaseOrPatronymicIgnoreCase(String name, String surname, String patronymic);
}
