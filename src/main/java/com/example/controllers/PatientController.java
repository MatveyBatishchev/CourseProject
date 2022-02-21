package com.example.controllers;

import com.example.models.Patient;
import com.example.models.Role;
import com.example.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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
    public String addNew(@ModelAttribute("patient")  Patient patient) {
        return "main/registration";
    }

    @PostMapping("/new")
    public String addNew(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "main/registration";
        Patient patientFromDb = patientRepository.findByEmail(patient.getEmail());
        if (patientFromDb != null) {
            model.addAttribute("message", "Пользователь с таким email уже существует!");
            return "main/registration";
        }
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        model.addAttribute("email", patient.getEmail());
        patientRepository.save(patient);
        return "main/login";
    }

    @GetMapping("/{id}/edit")
    public String editById( @PathVariable("id") Long id, Model model) {
        Optional<Patient> patient = patientRepository.findById(id);
        patient.ifPresent(value -> model.addAttribute("patient", value));
        return "patients/editById";
    }

    @PutMapping("/{id}")
    public String editById(@Valid @ModelAttribute("patient") Patient patient, Model model) {
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        patientRepository.save(patient);
        model.addAttribute("patient", patient);
        return "patients/getById";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        patientRepository.deleteById(id);
        return "redirect:/patients/all";
    }

    @PostMapping("/search")
    public String search(@RequestParam String search, Model model) {
        Iterable<Patient> patientsList = null;
        String[] searchArray = search.split(" ");
        if (search != null && !search.isEmpty()) {
            if (searchArray.length == 2)
            patientsList = patientRepository.findByNameIgnoreCaseAndSurnameIgnoreCase(searchArray[0],searchArray[1]);
            if (searchArray.length == 1)
                patientsList = patientRepository.findByNameIgnoreCaseOrSurnameIgnoreCase(searchArray[0],searchArray[0]);
        }
        else  patientsList = patientRepository.findAll();
        model.addAttribute("patients", patientsList);
        return "/patients/getAll";
    }

}
