package com.example.controllers;

import com.example.models.Doctor;
import com.example.models.Schedule;
import com.example.services.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // not used
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllSchedulesView(ModelAndView modelAndView) {
        return scheduleService.findAllSchedulesView(modelAndView);
    }

    // wait to be realized
    @GetMapping("/{id}")
    public ModelAndView getScheduleByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return scheduleService.findScheduleByIdView(id, modelAndView, "/schedules/getById");
    }


    @GetMapping("/new")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ModelAndView getAddNewScheduleView(@AuthenticationPrincipal Doctor doctor, ModelAndView modelAndView) {
        return scheduleService.findAddNewScheduleView(doctor, modelAndView);
    }

    // wait to be realized
    @GetMapping("/{id}/edit")
    public ModelAndView getEditScheduleByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return scheduleService.findScheduleByIdView(id, modelAndView, "schedules/editById");
    }

    @GetMapping("/doctor/{doctorId}")
    @ResponseBody
    public String getSchedulesOfDoctorJson(@PathVariable("doctorId") Long doctorId) {
        return scheduleService.findSchedulesByDoctorIdJson(doctorId);
    }

    @GetMapping("/{id}/timetables")
    @ResponseBody
    public String getTimeTablesOfScheduleJson(@PathVariable("id") Long id) {
        return scheduleService.findTimeTablesOfScheduleJson(id);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public String addNewSchedule(@ModelAttribute("schedule") Schedule schedule,
                                 @AuthenticationPrincipal Doctor doctor,
                                 @RequestParam("scheduleDate") String date,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime) {
        scheduleService.saveNewSchedule(schedule, date, startTime, endTime, doctor);
        return "redirect:/schedules/all";
    }

    // wait to be realized
    @DeleteMapping("/{id}")
    public void deleteScheduleById(@PathVariable("id") Long id) {
        scheduleService.deleteScheduleById(id);
    }

}
