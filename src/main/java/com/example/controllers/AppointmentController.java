package com.example.controllers;

import com.example.models.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @GetMapping("/new")
    public String addNew(@ModelAttribute("appointment") Patient patient) {
        return "appointments/newAppointment";
    }

}
