package com.example.services;

import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.models.TimeTable;
import com.example.repo.ScheduleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd-MM-yyyy").create();

    public Schedule findScheduleById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Расписание с id " + id + " не найдено!"));
    }

    public ModelAndView findAllSchedulesView(ModelAndView modelAndView) {
        modelAndView.addObject("schedules", scheduleRepository.findByOrderByIdAsc());
        modelAndView.setViewName("schedules/getAll");
        return modelAndView;
    }

    public ModelAndView findScheduleByIdView(Long id, ModelAndView modelAndView, String viewName) {
        Schedule scheduleById = findScheduleById(id);
        modelAndView.addObject("schedule", scheduleById);
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    public ModelAndView findAddNewScheduleView(Doctor doctor, ModelAndView modelAndView) {
        if (doctor != null) {
            modelAndView.addObject("schedules", gson.toJson(doctor.getSchedules()));
            modelAndView.addObject("schedule", new Schedule());
            modelAndView.setViewName("schedules/newSchedule");
        }
        return modelAndView;
    }

    public String findSchedulesByDoctorIdJson(Long id) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(id)
                .stream()
                .filter(schedule -> schedule.getTimeTable().stream().anyMatch(TimeTable::isAvailable))
                .collect(Collectors.toList());
        return gson.toJson(schedules);
    }

    public String findTimeTablesOfScheduleJson(Long id) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JSR310Module());
            List<TimeTable> timetables = scheduleRepository.findById(id).orElseThrow().getTimeTable()
                    .stream().sorted().filter(TimeTable::isAvailable).collect(Collectors.toList());
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

        while(!startDateTime.isAfter(endDateTime) && !startDateTime.equals(endDateTime)){
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

    public void deleteScheduleById(Long id) {
        scheduleRepository.deleteById(id);
    }

    // delete all schedules which date is expired for more than 5 days
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        java.util.Date currentDate = new java.util.Date();
        for (Schedule schedule : schedules) {
            if (currentDate.getTime() - schedule.getDate().getTime() > 4.32e+8) {
                scheduleRepository.delete(schedule);
            }
        }
    }

}



