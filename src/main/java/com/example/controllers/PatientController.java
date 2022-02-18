package com.example.controllers;

import com.example.models.Patient;
import com.example.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        Iterable<Patient> patients = patientRepository.findAll();
        ArrayList<Patient> patientsList = new ArrayList<>();
        patients.forEach(patientsList::add);
        Comparator<Patient> firstNameSorter = Comparator.comparing(Patient::getId);
        patientsList.sort(firstNameSorter);
        model.addAttribute("patients",patientsList);
        return "patients/getAll";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Model model) {
        Optional<Patient> patient = patientRepository.findById(id);
        patient.ifPresent(value -> model.addAttribute("patient", value));
        return "patients/getById";
    }

    @GetMapping("/new")
    public String addNewGet(@ModelAttribute("patient")  Patient patient) {
        return "main/registration";
    }

    @PostMapping("/new")
    public String addNewPost(@ModelAttribute("person") @Valid Patient person, BindingResult bindingResult) {
        patientRepository.save(person);
        return "redirect:/medtouch/home";
    }

    @GetMapping("/{id}/edit")
    public String editById( @PathVariable("id") Long id, Model model) {
        Optional<Patient> patient = patientRepository.findById(id);
        patient.ifPresent(value -> model.addAttribute("patient", value));
        return "patients/editById";
    }

    @PutMapping("/{id}")
    public String updateById(@ModelAttribute("patient") @Valid Patient patient, Model model, BindingResult bindingResult) {
        patientRepository.save(patient);
        System.out.println(patient.getSex());
        model.addAttribute("patient", patient);
        return "patients/getById";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        patientRepository.deleteById(id);
        return "redirect:/patients/all";
    }
}
