package com.example.controllers;

import com.example.models.Patient;
import com.example.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

}
