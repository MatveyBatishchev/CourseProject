package com.example.controllers;

import com.example.models.Appointment;
import com.example.models.Patient;
import com.example.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/all")
    public String getAllAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAllAppointmentsAsc());
        return "appointments/getAll";
    }

    @GetMapping("/{id}")
    public String getAppointmentById(@PathVariable("id") Long id, Model model) {
        Appointment appointmentById = appointmentService.findAppointmentById(id);
        if (appointmentById == null) {
            model.addAttribute("object", "Приём");
            return "mistakes/notFound";
        }
        model.addAttribute("appointment", appointmentById);
        return "appointments/getById";
    }

    @GetMapping("/new")
    public String addNewAppointment() {
        return "appointments/newAppointment";
    }

    @PostMapping("/new")
    @ResponseBody
    public String addNewAppointment(
            @AuthenticationPrincipal Patient patient,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("callbackInfo") String callbackInfo) {
        System.out.println(callbackInfo);
        appointmentService.saveAppointment(patient, date, time, doctorId, callbackInfo);
        return "/appointments/all";
    }

    @GetMapping("/{id}/edit")
    public String editAppointmentById(@PathVariable("id") Long id, Model model) {
        Appointment appointmentById = appointmentService.findAppointmentById(id);
        if (appointmentById == null) {
            model.addAttribute("object", "Приём");
            return "mistakes/notFound";
        }
        model.addAttribute("appointment", appointmentById);
        return "/appointments/editById";
    }

    @PutMapping("/{id}")
    public String editAppointmentById(@ModelAttribute("appointment") Appointment appointment, BindingResult bindingResult) {
        appointmentService.saveAppointment(appointment);
        return "redirect:/doctors/" + appointment.getId();
    }

    @PutMapping("/setStatus")
    @ResponseBody
    public String setAppointmentStatus(@RequestParam("status") int status,
                                       @RequestParam("appointmentId") Long appointmentId) {
        appointmentService.editAppointmentStatus(appointmentId, status);
        return "new status has been set";
    }

    @DeleteMapping("/{id}")
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
    }

}
