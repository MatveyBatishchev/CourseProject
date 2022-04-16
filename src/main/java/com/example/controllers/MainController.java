package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recoverymed")
public class MainController {

    @GetMapping("/home")
    public String homePage() {
        return "main/home";
    }

    @GetMapping("/doctors")
    public String doctorsPage() {
        return "main/doctors";
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
