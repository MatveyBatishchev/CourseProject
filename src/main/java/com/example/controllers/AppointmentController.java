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
        return "appointments/newAppointment1";
    }

    @PostMapping("/new")
    public String addNewAppointment(
            @AuthenticationPrincipal Patient patient,
            @ModelAttribute("appointment") Appointment appointment,
            @RequestParam("timetable") Long timeTableId) {
        System.out.println(timeTableId + "FUCCCKCKKCKC");
        appointmentService.saveAppointment(appointment, patient, timeTableId);
        return "redirect:/appointments/all";
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

    @DeleteMapping("/{id}")
    public void deleteAppointmentById(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
    }

}
