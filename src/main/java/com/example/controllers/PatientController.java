package com.example.controllers;

import com.example.models.Patient;
import com.example.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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
            return "mistakes/notFound";
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
            model.addAttribute("emailMessage", "Пользователь с таким email уже существует!");
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
            return "mistakes/notFound";
        }
        model.addAttribute("patient", patientById);
        return "patients/editById";
    }

    @PutMapping("/{id}")
    public String editPatientById(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                  @RequestParam("profileImage") MultipartFile multipartFile) {
        if (bindingResult.hasErrors()) return "/patients/editById";
        if (multipartFile.isEmpty()) patientService.editPatient(patient);
        else patientService.editPatientWithFile(patient, multipartFile);
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

    @GetMapping("/activate/{code}")
    public String activatePatientAccount(@PathVariable("code") String code, Model model) {
        boolean isActivated = patientService.activateUser(code);
        if (isActivated) model.addAttribute("activationMessage", "Email успешно подтверждён!");
        else model.addAttribute("activationMessage", "Код активации не был найден!");
        return "main/activationCode";
    }

    @PostMapping("/confirmEmail/{id}")
    @ResponseBody
    public String confirmPatientEmail(@PathVariable("id") Patient patient) {
        patientService.sendConfirmationEmail(patient.getEmail(), patient.getName(), patient.getActivationCode());
        return "Успешно!\n Письмо с подтверждением было отправлено вам на почту!";
    }

}
