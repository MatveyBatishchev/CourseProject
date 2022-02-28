package com.example.controllers;

import com.example.models.Patient;
import com.example.repo.PatientRepository;
import com.example.services.PatientService;
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

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/all")
    public String getAllPatients(Model model) {
        model.addAttribute("patients",patientService.findAllPatientsAsc());
        return "patients/getAll";
    }

    @GetMapping("/{id}")
    public String getPatientById(@PathVariable("id") Long id, Model model) {
        Patient patientById = patientService.findPatientById(id);
        if (patientById == null) {
            model.addAttribute("object", "Пациент");
            return "mistakes/notfound";
        }
        model.addAttribute("patient", patientById);
        return "patients/getById";
    }

    @GetMapping("/new")
    public String addNewPatient(@ModelAttribute("patient")  Patient patient) {
        return "main/registration";
    }

    @PostMapping("/new")
    public String addNewPatient(@ModelAttribute("patient") @Valid Patient patient,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "main/registration";

        if (patientService.findPatientByEmail(patient.getEmail()) != null) {
            model.addAttribute("message", "Пользователь с таким email уже существует!");
            return "main/registration";
        }

        patientService.savePatient(patient);
        model.addAttribute("email", patient.getEmail());
        return "main/login";
    }

    @GetMapping("/{id}/edit")
    public String editPatientById( @PathVariable("id") Long id, Model model) {
        Patient patientById = patientService.findPatientById(id);
        if (patientById == null) {
            model.addAttribute("object", "Пациент");
            return "mistakes/notfound";
        }
        model.addAttribute("patient", patientById);
        return "patients/editById";
    }

    @PutMapping("/{id}")
    public String editPatientById(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                  @RequestParam("avatar") MultipartFile multipartFile) {
        if (bindingResult.hasErrors()) return "/patients/editById";

        patientService.savePatientWithFile(patient, multipartFile);
        return "redirect:/patients/" + patient.getId();
    }

    @DeleteMapping("/{id}")
    public String deletePatientById(@PathVariable("id") Long id) {
        patientService.deletePatientById(id);
        return "redirect:/patients/all";
    }

    @PostMapping("/search")
    public String searchPatient(@RequestParam String search, Model model) {
        model.addAttribute("patients", patientService.searchPatientByString(search));
        return "/patients/getAll";
    }

}
