package com.example.repo;

import com.example.models.Doctor;
import com.example.models.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByOrderByIdAsc();
    List<Doctor> findByNameIgnoreCaseOrSurnameIgnoreCaseOrPatronymicIgnoreCase(String name, String surname, String patronymic);
    Doctor findByEmail(String email);

    @Query(value = "Select * from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id where ds.specialities=:speciality", nativeQuery = true)
    List<Doctor> findDoctorsBySpeciality(@Param("speciality") String speciality);



}
