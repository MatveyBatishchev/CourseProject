package com.example.repo;

import com.example.models.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalTime;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query(value="Select time_table.id\n" +
            "from time_table \n" +
            "inner join schedule on time_table.schedule_id = schedule.id\n" +
            "where schedule.doctor_id=:doctorId and \n" +
            "\t\tschedule.date>=:date and schedule.date<=:date and\n" +
            "\t\ttime_table.start_time=:time", nativeQuery = true)
    Long findTimeTableIdByDateAndTimeAbdDoctorId(@Param("date") Date date, @Param("time") LocalTime time, @Param("doctorId") Long doctorId);
}
