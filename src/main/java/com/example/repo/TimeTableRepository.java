package com.example.repo;

import com.example.models.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query(value="Select *\n" +
            "from time_table \n" +
            "inner join schedule on time_table.schedule_id = schedule.id\n" +
            "where schedule.doctor_id=:doctorId and \n" +
            "\t\tschedule.date>=:date and schedule.date<=:date and\n" +
            "\t\ttime_table.start_time=:time", nativeQuery = true)
    TimeTable findTimeTableIdByDateAndTimeAndDoctorId(@Param("date") Date date, @Param("time") LocalTime time, @Param("doctorId") Long doctorId);


    @Query(value="Select * from time_table t inner join schedule s\n" +
            "on t.schedule_id = s.id where doctor_id=:doctorId", nativeQuery = true)
    List<TimeTable> findByDoctorId(@Param("doctorId") Long doctorId);
}
