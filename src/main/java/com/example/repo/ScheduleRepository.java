package com.example.repo;

import com.example.models.Patient;
import com.example.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByOrderByIdAsc();

    @Query(value = "Select * from schedule where doctor_id=:param", nativeQuery = true)
    List<Schedule> findByDoctorId(@Param("param") Long id);
}
