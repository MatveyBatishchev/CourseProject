package com.example.controllers;

import com.example.models.Appointment;
import com.example.models.Patient;
import com.example.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        Long appointmentId = appointmentService.saveAppointment(patient, date, time, doctorId, callbackInfo);
        return "/appointments/success/" + appointmentId;
    }

    @GetMapping("/success/{id}")
    public String getResultAppointment(@PathVariable("id") Long appointmentId, Model model) {
        model.addAttribute("appointment", appointmentService.findAppointmentById(appointmentId));
        return "/appointments/success";
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


    @GetMapping("/exportPdf/{id}")
    public ResponseEntity<byte[]> getPDF(@PathVariable("id") Long appointmentId) {
        return appointmentService.exportPdf(appointmentId);
    }

}
