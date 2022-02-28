package com.example.controllers;

import com.example.models.Doctor;
import com.example.repo.DoctorRepository;
import com.example.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorRepository doctorRepository, DoctorService doctorService) {
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
    }

    @GetMapping("/all")
    public String getAllDoctors(Model model) {
        model.addAttribute("doctors",doctorService.findAllDoctorsAsc());
        return "doctors/getAll";
    }

    @GetMapping("/{id}")
    public String getDoctorById(@PathVariable("id") Long id, Model model) {
        Doctor doctorById = doctorService.findDoctorById(id);
        if (doctorById == null) {
            model.addAttribute("object", "Доктор");
            return "mistakes/notfound";
        }
        model.addAttribute("doctor", doctorById);
        return "doctors/getById";
    }

    @GetMapping("/new")
    public String addNewDoctor(@ModelAttribute("doctor") Doctor doctor) {
        return "doctors/newDoctor";
    }

    @PostMapping("/new")
    public String addNewDoctor(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult,
                               @RequestParam("avatar") MultipartFile multipartFile) {
        if (bindingResult.hasErrors()) return "doctors/newDoctor";
        doctorService.saveDoctorWithFile(doctor, multipartFile);
        return "redirect:/doctors/all";
    }

    @GetMapping("/{id}/edit")
    public String editDoctorById( @PathVariable("id") Long id, Model model) {
        Doctor doctorById = doctorService.findDoctorById(id);
        if (doctorById == null) {
            model.addAttribute("object", "Доктор");
            return "mistakes/notfound";
        }
        model.addAttribute("doctor", doctorById);
        return "doctors/editById";
    }

    @PutMapping("/{id}")
    public String editDoctorById(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult,
                                 @RequestParam("avatar") MultipartFile multipartFile) {
        if (bindingResult.hasErrors()) return "doctors/editById";
        doctorService.saveDoctorWithFile(doctor, multipartFile);
        return "redirect:/doctors/" + doctor.getId();
    }

    @DeleteMapping("/{id}")
    public String deletePatientById(@PathVariable("id") Long id) {
        doctorService.deleteDoctorById(id);
        return "redirect:/doctors/all";
    }

    @PostMapping("/search")
    public String searchPatient(@RequestParam String search, Model model) {
        model.addAttribute("doctors", doctorService.searchDoctorsByString(search));
        return "/doctors/getAll";
    }

}
