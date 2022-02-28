package com.example.controllers;

import com.example.models.Appointment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @GetMapping("/new")
    public String addNewAppointment(@ModelAttribute("appointment") Appointment appointment) {
        return "appointments/newAppointment";
    }

    @PostMapping("/new")
    public String addNewAppointment(@ModelAttribute("appointment") Appointment appointment, BindingResult bindingResult) {
        return "redirect:medtouch/home";
    }

}
