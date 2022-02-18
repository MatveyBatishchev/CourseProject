package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medtouch")
public class MainController {

    @GetMapping("/home")
    public String homePage() {
        return "main/home";
    }

    @GetMapping("/aboutUs")
    public String aboutUsPage() {
        return "main/aboutUs";
    }

    @GetMapping("/contacts")
    public String contactsPage() {
        return "main/contacts";
    }

    @GetMapping("/login")
    public String login() {
        return "main/login";
    }

}
