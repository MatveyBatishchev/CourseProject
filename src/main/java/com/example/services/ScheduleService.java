package com.example.services;

import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.models.TimeTable;
import com.example.repo.ScheduleRepository;
import com.example.repo.TimeTableRepository;
import com.example.util.ScheduleSerializer;
import com.example.util.TimeTableSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final ScheduleRepository scheduleRepository;
    private final TimeTableRepository timeTableRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JSR310Module());
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

    public ModelAndView findDoctorSchedulesCalendarView(Doctor doctor, ModelAndView modelAndView) {
        try {
            String timetableEvents = new ObjectMapper().registerModule(new SimpleModule()
                    .addSerializer(TimeTable.class, new TimeTableSerializer())).writeValueAsString(timeTableRepository.findByDoctorId(doctor.getId()));
            modelAndView.addObject("timetableEvents", timetableEvents);
            String schedules = objectMapper.registerModule(new SimpleModule().addSerializer(Schedule.class, new ScheduleSerializer())).writeValueAsString(scheduleRepository.findByDoctorId(doctor.getId()));
            modelAndView.addObject("schedulesJson", schedules);
            modelAndView.addObject("doctor", doctor);
            modelAndView.setViewName("schedules/doctorsSchedules");
            return modelAndView;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ModelAndView findDoctorSchedulesView(Doctor doctor, ModelAndView modelAndView, String viewName) {
        if (doctor != null) {
            modelAndView.addObject("schedules", gson.toJson(doctor.getSchedules()));
            modelAndView.addObject("doctor", doctor);
            modelAndView.setViewName(viewName);
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

    public String findTimeTablesOfScheduleJson(Long id, boolean getAll) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JSR310Module());
            List<TimeTable> timetables;
            if (getAll) {
                timetables = scheduleRepository.findById(id).orElseThrow().getTimeTable().stream().sorted().collect(Collectors.toList());
            }
            else {
                timetables = scheduleRepository.findById(id).orElseThrow().getTimeTable()
                        .stream().sorted().filter(TimeTable::isAvailable).collect(Collectors.toList());
            }
            return mapper.writeValueAsString(timetables);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public String getPreviewOfTimetablesJson(String previewSchedule, String breakTime, Integer breakDuration) {
        try {
            Schedule schedule = objectMapper.readValue(previewSchedule, Schedule.class);
            Date scheduleDate = schedule.getDate();
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();
            Integer consultingTime = schedule.getConsultingTime();

            LocalTime breakStartTime = LocalTime.parse(breakTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime breakEndTime = breakStartTime.plus(breakDuration, ChronoUnit.MINUTES);
            Set<TimeTable> times = new HashSet<>();

            while (!startTime.plus(consultingTime, ChronoUnit.MINUTES).isAfter(breakStartTime) && startTime != breakStartTime) {
                TimeTable timeTable = new TimeTable();
                timeTable.setStartTime(startTime);
                times.add(timeTable);
                startTime = startTime.plus(consultingTime, ChronoUnit.MINUTES);
            }
            startTime = breakEndTime;
            while (!startTime.plus(consultingTime, ChronoUnit.MINUTES).isAfter(endTime) && startTime != endTime) {
                TimeTable timeTable = new TimeTable();
                timeTable.setStartTime(startTime);
                times.add(timeTable);
                startTime = startTime.plus(consultingTime, ChronoUnit.MINUTES);
            }

            return objectMapper.writeValueAsString(times.stream().sorted(Comparator.comparing(TimeTable::getStartTime)).collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveNewSchedule(String scheduleJson, String timetablesJson, Doctor doctor) {
        try {
            Schedule schedule = objectMapper.readValue(scheduleJson, Schedule.class);
            List<TimeTable> timeTables = objectMapper.readValue(timetablesJson, new TypeReference<List<TimeTable>>(){});
            schedule.setDoctor(doctor);
            Date date = schedule.getDate();

            timeTables.forEach(t -> {
                t.setSchedule(schedule);
                t.setDate(date);
                t.setAvailable(true);
            });
            schedule.setTimeTable(new HashSet<>(timeTables));
            scheduleRepository.save(schedule);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

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

    public void moveSchedule(Long id, String moveDate) {
        try {
            Schedule schedule = findScheduleById(id);
            java.util.Date date = new SimpleDateFormat("dd-MM-yyyy").parse(moveDate);
            java.sql.Date moveSqlDate = new java.sql.Date(date.getTime());
            Schedule conflictSchedule = scheduleRepository.findByDate(moveSqlDate);
            if (conflictSchedule != null) {
                scheduleRepository.delete(conflictSchedule);
            }
            schedule.setDate(moveSqlDate);
            schedule.getTimeTable().forEach(t -> t.setDate(moveSqlDate));
            scheduleRepository.save(schedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void copySchedule(Long id, String copyDate) {
        try {
            Schedule schedule = findScheduleById(id);
            java.util.Date date = new SimpleDateFormat("dd-MM-yyyy").parse(copyDate);
            java.sql.Date copySqlDate = new java.sql.Date(date.getTime());
            Schedule conflictSchedule = scheduleRepository.findByDate(copySqlDate);
            if (conflictSchedule != null) {
                scheduleRepository.delete(conflictSchedule);
            }
            Schedule copySchedule = new Schedule();
            copySchedule.setDate(copySqlDate);
            copySchedule.setDoctor(schedule.getDoctor());
            copySchedule.setConsultingTime(schedule.getConsultingTime());
            copySchedule.setStartTime(schedule.getStartTime());
            copySchedule.setEndTime(schedule.getEndTime());
            schedule.getTimeTable().stream().forEach(t -> {
                TimeTable timeTable = new TimeTable();
                timeTable.setStartTime(t.getStartTime());
                timeTable.setDate(copySqlDate);
                timeTable.setAvailable(true);
                timeTable.setSchedule(copySchedule);
                copySchedule.getTimeTable().add(timeTable);
            });
            scheduleRepository.save(copySchedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void editScheduleTimetables(Long id, String timetablesJson) {
        try {
            Schedule schedule = findScheduleById(id);
            System.out.println(timetablesJson);
            List<TimeTable> timeTables = objectMapper.readValue(timetablesJson, new TypeReference<List<TimeTable>>(){});
            System.out.println(timeTables);
            Date date = schedule.getDate();

            timeTables.forEach(t -> {
                t.setSchedule(schedule);
                t.setDate(date);
            });
            schedule.getTimeTable().clear();
            schedule.getTimeTable().addAll(timeTables);
            scheduleRepository.save(schedule);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void deleteScheduleById(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<TimeTable> getTimetables(LocalTime startTime, LocalTime endTime, Integer consultingTime,
                                         LocalTime breakTime, Integer breakDuration) {
        return null;
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



