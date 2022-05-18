package com.example.repo;

import com.example.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByOrderByIdAsc();

//    @Query(value = "Select * from schedule where doctor_id=:param", nativeQuery = true)
//    List<Schedule> findByDoctorId(@Param("param") Long id);

    List<Schedule> findByDoctorId(Long id);

    Schedule findByDate(Date date);
}
