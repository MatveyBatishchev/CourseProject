package com.example.repo;

import com.example.models.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findAll(Pageable pageable);
    List<Doctor> findByOrderByIdAsc();
    List<Doctor> findByNameIgnoreCaseOrSurnameIgnoreCaseOrPatronymicIgnoreCase(String name, String surname, String patronymic);
    Doctor findByEmail(String email);

    @Query(value = "Select * from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id where ds.specialities=:speciality", nativeQuery = true)
    List<Doctor> findDoctorsBySpeciality(@Param("speciality") String speciality);

    @Query(value = "Select * from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id where ds.specialities=:speciality", nativeQuery = true)
    Page<Doctor> findDoctorsBySpeciality(@Param("speciality") String speciality, Pageable pageable);

    @Query(value = """
            Select * from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id
            where ds.specialities=:speciality
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))""",
            countQuery = """
            Select count(*) from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id
            where ds.specialities=:speciality
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))""",
            nativeQuery = true)
    Page<Doctor> findDoctorBySpecialityAndOneString(@Param("speciality") String speciality, @Param("name") String name, Pageable pageable);

    @Query(value = """
            Select * from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id
            where ds.specialities=:speciality
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))""",
            countQuery = """
            Select count(*) from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id
            where ds.specialities=:speciality
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))""",
            nativeQuery = true)
    Page<Doctor> findDoctorBySpecialityAndTwoStrings(@Param("speciality") String speciality, @Param("name") String name, @Param("surname") String surname, Pageable pageable);

    @Query(value = """
            Select * from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id
            where ds.specialities=:speciality
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :patronymic, ' %'))""",
            countQuery = """
            Select count(*) from doctor d inner join doctor_speciality ds on d.id=ds.doctor_id
            where ds.specialities=:speciality
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :patronymic, ' %'))""",
            nativeQuery = true)
    Page<Doctor> findDoctorBySpecialityAndThreeStrings(@Param("speciality") String speciality, @Param("name") String name, @Param("surname") String surname, @Param("patronymic") String patronymic, Pageable pageable);

    @Query(value = """
            Select * from doctor
            where LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))""",
            countQuery = """
            Select count(*) from doctor
            where LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))""",
            nativeQuery = true)
    Page<Doctor> findDoctorByOneString(@Param("name") String name, Pageable pageable);

    @Query(value = """
            Select * from doctor
            where LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))""",
            countQuery = """
            Select count(*) from doctor
            where LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))""",
            nativeQuery = true)
    Page<Doctor> findDoctorByTwoStrings(@Param("name") String name, @Param("surname") String surname, Pageable pageable);

    @Query(value = """
            Select * from doctors
            where LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :patronymic, ' %'))""",
            countQuery = """
            Select count(*) from doctors
            where LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :name, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :surname, ' %'))
            and LOWER(CONCAT(' ', name, ' ', surname, ' ', patronymic, ' ')) like LOWER(CONCAT('% ', :patronymic, ' %'))""",
            nativeQuery = true)
    Page<Doctor> findDoctorByThreeStrings(@Param("name") String name, @Param("surname") String surname, @Param("patronymic") String patronymic, Pageable pageable);


}
