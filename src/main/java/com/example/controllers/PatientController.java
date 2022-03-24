package com.example.controllers;

import com.example.models.Patient;
import com.example.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getAllPatients(ModelAndView modelAndView) {
        return patientService.findAllPatients(modelAndView);
    }

    @GetMapping("/{id}")
    public ModelAndView getPatientById(@PathVariable("id") Long patientId, ModelAndView modelAndView) {
        return patientService.findPatientById(patientId, modelAndView);
    }

    @GetMapping("/search")
    public String getPatientBySearch(@RequestParam("search") String search, Model model) {
        model.addAttribute("patients", patientService.findPatientBySearch(search));
        return "/patients/getAll";
    }

    @GetMapping("/new")
    public String addNewPatient(@ModelAttribute("patient")  Patient patient) {
        return "main/registration";
    }

    @PostMapping("/new")
    public ModelAndView addNewPatient(@ModelAttribute("patient") @Valid Patient patient,
                                BindingResult bindingResult, ModelAndView modelAndView) {
        return patientService.saveNewPatient(patient, bindingResult, modelAndView);
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editPatientById( @PathVariable("id") Long patientId, ModelAndView modelAndView) {
        return patientService.findPatientByIdForEdit(patientId, modelAndView);
    }

    @PutMapping("/{id}")
    public ModelAndView editPatientById(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                  @RequestParam("profileImage") MultipartFile multipartFile, ModelAndView modelAndView) {
        return patientService.editPatient(patient, bindingResult, multipartFile, modelAndView);
    }

    @DeleteMapping("/{id}")
    public ModelAndView deletePatientById(@PathVariable("id") Long patientId, ModelAndView modelAndView) {
        return patientService.deletePatientById(patientId, modelAndView);
    }

    @GetMapping("/activate/{code}")
    public ModelAndView activatePatientAccount(@PathVariable("code") String code, ModelAndView modelAndView) {
        return patientService.activatePatient(code, modelAndView);
    }

    @PostMapping("/confirmEmail/{id}")
    @ResponseBody
    public String confirmPatientEmail(@PathVariable("id") Patient patient) {
        patientService.sendConfirmationEmail(patient.getEmail(), patient.getName(), patient.getActivationCode());
        return "Успешно!\n Письмо с подтверждением было отправлено вам на почту!";
    }

}
