package com.example.controllers;

import com.example.models.Appointment;
import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.sql.Date;

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
            return "mistakes/notfound";
        }
        model.addAttribute("appointment", appointmentById);
        return "appointments/getById";
    }

    @GetMapping("/new")
    public String addNewAppointment(@ModelAttribute("appointment") Appointment appointment) {
        return "appointments/newAppointment";
    }

    @PostMapping("/new")
    public String addNewAppointment(
            @AuthenticationPrincipal Patient patient,
            @ModelAttribute("appointment") Appointment appointment,
            @RequestParam("time") String time) {
        appointmentService.saveAppointment(appointment, patient, time);
        return "redirect:/appointments/all";
    }

    @GetMapping("/{id}/edit")
    public String editAppointmentById(@PathVariable("id") Long id, Model model) {
        Appointment appointmentById = appointmentService.findAppointmentById(id);
        if (appointmentById == null) {
            model.addAttribute("object", "Приём");
            return "mistakes/notfound";
        }
        model.addAttribute("appointment", appointmentById);
        return "/appointments/editById";
    }

    @PutMapping("/{id}")
    public String editAppointmentById(@ModelAttribute("appointment") Appointment appointment, BindingResult bindingResult) {
        appointmentService.saveAppointment(appointment);
        return "redirect:/doctors/" + appointment.getId();
    }

    @DeleteMapping("/{id}")
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
    }

}
