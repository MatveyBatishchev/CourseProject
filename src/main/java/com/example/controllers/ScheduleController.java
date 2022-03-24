package com.example.controllers;

import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.services.ScheduleService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public String addNewSchedule(@AuthenticationPrincipal Doctor doctor,
                                 @ModelAttribute("schedule") Schedule schedule,
                                 Model model) {
        if (doctor != null) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd-MM-yyyy").create();
            model.addAttribute("schedules", gson.toJson(doctor.getSchedules()));
        }
        return "schedules/newSchedule";
    }

    @PostMapping("/new")
    public String addNewSchedule(@ModelAttribute("schedule") Schedule schedule,
                                 @AuthenticationPrincipal Doctor doctor,
                                 @RequestParam("scheduleDate") String date,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime) {
        scheduleService.saveNewSchedule(schedule, date, startTime, endTime, doctor);
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
        //scheduleService.saveNewSchedule(schedule, startTime, endTime, doctor);
        return "redirect:/schedules/" + schedule.getId();
    }

    @DeleteMapping("/{id}")
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        scheduleService.deleteScheduleById(id);
    }

    @GetMapping("/getDoctorSchedules")
    @ResponseBody
    public String getSchedulesOfDoctor(@RequestParam("doctorId") Long doctorId) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd-MM-yyyy").create();
        return gson.toJson(scheduleService.findSchedulesByDoctorId(doctorId));
    }

    @GetMapping("/getTimeTables")
    @ResponseBody
    public String getTimeTablesOfSchedule(@RequestParam("scheduleDate") String scheduleDate,
                                          @RequestParam("doctorId") Long doctorId) {
        return scheduleService.findTimeTablesOfSchedule(scheduleDate, doctorId);
    }

}
