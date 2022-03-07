package com.example.services;

import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.models.TimeTable;
import com.example.repo.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> findAllSchedules() {
        return scheduleRepository.findByOrderByIdAsc();
    }

    public Schedule findScheduleById(Long id) {
        Optional<Schedule> scheduleById = scheduleRepository.findById(id);
        return scheduleById.isPresent()? scheduleById.get() : null;
    }

    public void saveNewSchedule(Schedule schedule, String startTime, String endTime, Doctor doctor) {

        DateTimeFormatter DATE_TIME_FORMATTER =  DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startDateTime = LocalTime.parse(startTime, DATE_TIME_FORMATTER);
        LocalTime endDateTime = LocalTime.parse(endTime, DATE_TIME_FORMATTER);
        schedule.setStartTime(startDateTime);
        schedule.setEndTime(endDateTime);


        Date date = schedule.getDate();
        Set<TimeTable> times = schedule.getTimeTable();

        while(!startDateTime.isAfter(endDateTime)){
            TimeTable timeTable = new TimeTable();
            timeTable.setStartTime(startDateTime);
            timeTable.setAvailable(true);
            timeTable.setDate(date);

            times.add(timeTable);
            timeTable.setSchedule(schedule);
            startDateTime = startDateTime.plus(schedule.getConsultingTime(), ChronoUnit.MINUTES);
        }
        schedule.setTimeTable(times);
        schedule.setDoctor(doctor);
        scheduleRepository.save(schedule);
    }

    public void deleteScheduleById(Long id) {
        scheduleRepository.deleteById(id);
    }


    public List<Schedule> findSchedulesByDoctorId(Long id) {
        return scheduleRepository.findByDoctorId(id);
    }

}



