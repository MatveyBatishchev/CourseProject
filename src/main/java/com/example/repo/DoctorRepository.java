package com.example.repo;

import com.example.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByOrderByIdAsc();
    List<Doctor> findByNameIgnoreCaseOrSurnameIgnoreCaseOrPatronymicIgnoreCase(String name, String surname, String patronymic);
}
