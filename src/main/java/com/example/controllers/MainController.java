package com.example.controllers;

import com.example.models.Doctor;
import com.example.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recoverymed")
public class MainController {

    private final DoctorService doctorService;

    @Autowired
    public MainController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/home")
    public String homePage() {
        return "main/home";
    }

    @GetMapping("/doctors")
    public String doctorsPage(Model model) {
        Page<Doctor> page = doctorService.findAllDoctorsWithPage(0);
        Map<Integer, List<Doctor>> doctorMap = new HashMap<>();
        doctorMap.put(1, page.stream().limit(4).collect(Collectors.toList()));
        if (page.getNumberOfElements() > 4) {
            doctorMap.put(2, page.getContent().subList(4, page.getNumberOfElements()));
        }
        model.addAttribute("doctors", doctorMap);
        model.addAttribute("totalPages", page.getTotalPages());
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
