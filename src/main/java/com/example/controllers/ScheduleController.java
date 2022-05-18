package com.example.controllers;

import com.example.models.Doctor;
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
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllSchedulesView(ModelAndView modelAndView) {
        return scheduleService.findAllSchedulesView(modelAndView);
    }

    // wait to be realized
    @GetMapping("/{id}")
    public ModelAndView getScheduleByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return scheduleService.findScheduleByIdView(id, modelAndView, "/schedules/getById");
    }

    @GetMapping("/doctor/{id}/view")
    public ModelAndView getDoctorsSchedulesView(@AuthenticationPrincipal Doctor doctor, ModelAndView modelAndView) {
        return scheduleService.findDoctorSchedulesCalendarView(doctor, modelAndView);
    }


    @GetMapping("/new")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ModelAndView getAddNewScheduleView(@AuthenticationPrincipal Doctor doctor, ModelAndView modelAndView) {
        return scheduleService.findDoctorSchedulesView(doctor, modelAndView, "schedules/newSchedule");
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
    public String getAvailableTimeTablesOfScheduleJson(@PathVariable("id") Long id) {
        return scheduleService.findTimeTablesOfScheduleJson(id, false);
    }

    @GetMapping("/{id}/timetables/all")
    @ResponseBody
    public String getAllTimeTablesOfScheduleJson(@PathVariable("id") Long id) {
        return scheduleService.findTimeTablesOfScheduleJson(id, true);
    }

    @GetMapping("/preview/timetables")
    @ResponseBody
    public String getPreviewOfTimetablesJson(@RequestParam("previewSchedule") String previewSchedule,
                                             @RequestParam("breakTime") String breakTime,
                                             @RequestParam("breakDuration") Integer breakDuration) {
        return scheduleService.getPreviewOfTimetablesJson(previewSchedule, breakTime, breakDuration);
    }

    @PostMapping("/new")
//    @PreAuthorize("hasAuthority('DOCTOR')")
    @ResponseBody
    public String addNewSchedule(@AuthenticationPrincipal Doctor doctor,
                                 @RequestParam("scheduleJson") String scheduleJson,
                                 @RequestParam("timetablesJson") String timetablesJson) {
        scheduleService.saveNewSchedule(scheduleJson, timetablesJson, doctor);
        return "/schedules/doctor/" + doctor.getId() + "/view";
    }

    @PostMapping("/{id}/move")
    @ResponseBody
    public void moveSchedule(@PathVariable("id") Long id,
                             @RequestParam("moveDate") String moveDate) {
        scheduleService.moveSchedule(id, moveDate);
    }

    @PostMapping("/{id}/copy")
    @ResponseBody
    public void copySchedule(@PathVariable("id") Long id,
                             @RequestParam("copyDate") String copyDate) {
        scheduleService.copySchedule(id, copyDate);
    }

    @PutMapping("/{id}/edit-timetables")
    @ResponseBody
    public void editScheduleTimetables(@PathVariable("id") Long id,
                                       @RequestParam("timetablesJson") String timetablesJson) {
        scheduleService.editScheduleTimetables(id, timetablesJson);
    }

    // wait to be realized
    @DeleteMapping("/{id}")
    public void deleteScheduleById(@PathVariable("id") Long id) {
        scheduleService.deleteScheduleById(id);
    }

}
