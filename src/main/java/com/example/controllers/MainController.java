package com.example.controllers;

import com.example.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/recoverymed")
public class MainController {

    private final DoctorService doctorService;

    @GetMapping()
    public String homePage() {
        return "main/home";
    }

    @GetMapping("/doctors")
    public ModelAndView doctorsPage(ModelAndView modelAndView) {
        return doctorService.findMainDoctorsFirstPageView(modelAndView);
    }

    @GetMapping("/services")
    public String servicesPage() {
        return "main/services";
    }

    @GetMapping("/aboutUs")
    public String aboutUsPage() {
        return "main/aboutUs";
    }

    @GetMapping("/contacts")
    public String contactsPage() {
        return "main/contacts";
    }

}
