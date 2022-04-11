package com.example.services;

import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.models.TimeTable;
import com.example.repo.ScheduleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Schedule> findSchedulesByDoctorId(Long id) {
        return scheduleRepository.findByDoctorId(id).stream().filter(schedule -> {
            boolean Available = false;
            for (TimeTable timeTable : schedule.getTimeTable()) {
                if (timeTable.isAvailable()) {
                    Available =  true;
                    break;
                }
            }
            return Available;
        }).collect(Collectors.toList());
    }

    public Schedule findScheduleByDate(Date date, Long doctorId) {
        return scheduleRepository.findByDateAndDoctorId(date, doctorId);
    }

    public String findTimeTablesOfSchedule(String scheduleDate, Long doctorId) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JSR310Module());

            List<TimeTable> timetables = findScheduleByDate(parseToDate(scheduleDate), doctorId)
                    .getTimeTable().stream().sorted().filter(TimeTable::isAvailable)
                    .collect(Collectors.toList());

            return mapper.writeValueAsString(timetables);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public void saveNewSchedule(Schedule schedule, String date, String startTime, String endTime, Doctor doctor) {

        DateTimeFormatter DATE_TIME_FORMATTER =  DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startDateTime = LocalTime.parse(startTime, DATE_TIME_FORMATTER);
        LocalTime endDateTime = LocalTime.parse(endTime, DATE_TIME_FORMATTER);
        schedule.setStartTime(startDateTime);
        schedule.setEndTime(endDateTime);

        schedule.setDate(parseToDate(date));

        Date scheduleDate = schedule.getDate();
        Set<TimeTable> times = schedule.getTimeTable();

        while(!startDateTime.isAfter(endDateTime)){
            TimeTable timeTable = new TimeTable();
            timeTable.setStartTime(startDateTime);
            timeTable.setAvailable(true);
            timeTable.setDate(scheduleDate);

            times.add(timeTable);
            timeTable.setSchedule(schedule);
            startDateTime = startDateTime.plus(schedule.getConsultingTime(), ChronoUnit.MINUTES);
        }
        schedule.setTimeTable(times);
        schedule.setDoctor(doctor);
        doctor.getSchedules().add(schedule);
        scheduleRepository.save(schedule);
    }

    public void deleteScheduleById(Long id) {
        scheduleRepository.deleteById(id);
    }

    public java.sql.Date parseToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        java.sql.Date sqlDate = null;
        try {
            java.util.Date parsed = format.parse(date);
            sqlDate = new Date(parsed.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sqlDate;
    }

    // delete all schedules which date is expired for more than 5 days
    @Scheduled(cron = "0 0 12 * * *")
    public void deleteExpiredSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        java.util.Date currentDate = new java.util.Date();
        for (Schedule schedule : schedules) {
            if (Math.abs(schedule.getDate().getTime() - currentDate.getTime()) > 432000000) {
                scheduleRepository.delete(schedule);
            }
        }
    }

}



