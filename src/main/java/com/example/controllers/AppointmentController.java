package com.example.controllers;

import com.example.models.Patient;
import com.example.services.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllAppointmentsView(ModelAndView modelAndView) {
        modelAndView.addObject("appointments", appointmentService.findAllAppointmentsAsc());
        modelAndView.setViewName("appointments/getAll");
        return modelAndView;
    }

    @GetMapping("/new")
    public String getNewAppointmentView() {
        return "appointments/newAppointment";
    }

    // waiting for realization
    @GetMapping("/{id}")
    public ModelAndView getAppointmentByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return appointmentService.findAppointmentByIdView(id, modelAndView, "appointments/getById");
    }

    @GetMapping("/{id}/success")
    public ModelAndView getSuccessAppointmentView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return appointmentService.findAppointmentByIdView(id, modelAndView, "appointments/success");
    }

    @GetMapping("/{id}/exportPdf")
    public ResponseEntity<byte[]> getPDF(@PathVariable("id") Long appointmentId) {
        return appointmentService.exportPdf(appointmentId);
    }

    @PostMapping("/new")
    @ResponseBody
    public String addNewAppointment(
            @AuthenticationPrincipal Patient patient,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("callbackInfo") String callbackInfo) {
        Long appointmentId = appointmentService.saveAppointment(patient, date, time, doctorId, callbackInfo);
        return "/appointments/" + appointmentId + "/success";
    }

    // waiting for realization
    @PutMapping("/{id}/setStatus")
    @ResponseBody
    public void setAppointmentStatus(@PathVariable("id") Long id,
                                     @RequestParam("status") Integer status) {
        appointmentService.editAppointmentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'USER', 'DOCTOR'})")
    @ResponseBody
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
    }

}
