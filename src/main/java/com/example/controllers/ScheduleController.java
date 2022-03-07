package com.example.controllers;

import com.example.models.Appointment;
import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/all")
    public String getAllSchedules(Model model) {
        model.addAttribute("schedules", scheduleService.findAllSchedules());
        return "schedules/getAll";
    }

    @GetMapping("/{id}")
    public String getScheduleById(@PathVariable("id") Long id, Model model) {
        Schedule scheduleById = scheduleService.findScheduleById(id);
        if (scheduleById == null) {
            model.addAttribute("object", "Расписание");
            return "mistakes/notFound";
        }
        model.addAttribute("schedule", scheduleById);
        return "schedules/getById";
    }

    @GetMapping("/new")
    public String addNewSchedule(@ModelAttribute("schedule") Schedule schedule) {
        return "schedules/newSchedule";
    }

    @PostMapping("/new")
    public String addNewSchedule(@ModelAttribute("schedule") Schedule schedule,
                                 @AuthenticationPrincipal Doctor doctor,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime) {
        scheduleService.saveNewSchedule(schedule, startTime, endTime, doctor);
        return "redirect:/schedules/all";
    }

    @GetMapping("/{id}/edit")
    public String editScheduleById(@PathVariable("id") Long id, Model model) {
        Schedule scheduleById = scheduleService.findScheduleById(id);
        if (scheduleById == null) {
            model.addAttribute("object", "Расписание");
            return "notFound";
        }
        model.addAttribute("schedule", scheduleById);
        return "schedules/editById";
    }

    @PutMapping("/{id}")
    public String editScheduleById(@ModelAttribute("schedule") Schedule schedule,
                                      @AuthenticationPrincipal Doctor doctor,
                                      @RequestParam("startTime") String startTime,
                                      @RequestParam("endTime") String endTime) {
        scheduleService.saveNewSchedule(schedule, startTime, endTime, doctor);
        return "redirect:/schedules/" + schedule.getId();
    }

    @DeleteMapping("/{id}")
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        scheduleService.deleteScheduleById(id);
    }

    @GetMapping("/getDoctorsSchedules")
    public String getTimeOfDoctor(Model model,
                                  @RequestParam("speciality") String speciality,
                                  @ModelAttribute("appointment") Appointment appointment) {
        model.addAttribute("doctor", appointment.getDoctor());
        model.addAttribute("selectedSpec", speciality);
        model.addAttribute("schedules", scheduleService.findSchedulesByDoctorId(appointment.getDoctor().getId()));
        return "appointments/newAppointment3";
    }

    @GetMapping("/getTimeTables")
    public String getTimeTablesOfSchedule(Model model,
                                          @ModelAttribute("appointment") Appointment appointment,
                                          @RequestParam("speciality") String speciality,
                                          @RequestParam("schedule") Long scheduleId) {
        model.addAttribute("selectedSpec", speciality);
        appointment.setDate(scheduleService.findScheduleById(scheduleId).getDate());
        model.addAttribute("timeTables", scheduleService.findScheduleById(scheduleId).getTimeTable());
        return "appointments/newAppointment4";
    }

}
