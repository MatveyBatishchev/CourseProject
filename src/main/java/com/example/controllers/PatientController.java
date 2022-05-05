package com.example.controllers;

import com.example.models.Patient;
import com.example.services.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority({'ADMIN','DOCTOR'})")
    public ModelAndView getAllPatientsWithPageView(ModelAndView modelAndView) {
        return patientService.findAllPatientsWithPageView(modelAndView);
    }

    @GetMapping("/new")
    public String getAddNewPatientView(@ModelAttribute("patient") Patient patient) {
        return "main/registration";
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAnyAuthority({'ADMIN','DOCTOR'})")
    public ModelAndView getPatientByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return patientService.findPatientById(id, modelAndView, "patients/getById");
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("(#id == authentication.principal.id and hasAuthority({'USER'})) or hasAuthority({'ADMIN'})")
    public ModelAndView getEditPatientByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return patientService.findPatientById(id, modelAndView, "patients/editById");
    }

    @GetMapping("/reset/{code}")
    public ModelAndView getResetPatientPasswordView(@PathVariable("code") String code, ModelAndView modelAndView) {
        return patientService.resetPassword(code, modelAndView);
    }

    @GetMapping("/activate/{code}")
    public ModelAndView activatePatientAccount(@PathVariable("code") String code, ModelAndView modelAndView) {
        return patientService.activatePatient(code, modelAndView);
    }

    @GetMapping("/all/page/{pageNumber}")
    @PreAuthorize("hasAnyAuthority({'ADMIN','DOCTOR'})")
    @ResponseBody
    public String getPatientsWithPageJson(@PathVariable("pageNumber") Integer pageNumber) {
        return patientService.findPatientsWithPageJson(pageNumber);
    }

    @GetMapping("/by-search")
    @PreAuthorize("hasAnyAuthority({'ADMIN','DOCTOR'})")
    @ResponseBody
    public String getPatientBySearchWithPageJson(@RequestParam("search") String search, @RequestParam("pageNumber") Integer pageNumber) {
        return patientService.findPatientBySearchWithPageJson(search, pageNumber);
    }

    @GetMapping("/email-exists")
    @ResponseBody
    public String checkPatientExistJson(@RequestParam("resetEmail") String resetEmail) {
        return patientService.checkIfPatientExists(resetEmail);
    }

    @GetMapping("/email-reset")
    @ResponseBody
    public String sendResetPasswordEmail(@RequestParam("resetEmail") String resetEmail) {
        patientService.sendResetPasswordEmail(resetEmail);
        return "Письмо для восстановления пароля было успешно отправлено вам на почту!";
    }

    @GetMapping("/compare-passwords")
    @PreAuthorize("#id == authentication.principal.id and hasAuthority({'USER'})")
    @ResponseBody
    public String comparePasswordsJson(@RequestParam("providedPassword") String providedPassword, @RequestParam("patientId") Long id) {
        return patientService.comparePasswords(providedPassword, id);
    }

    @PostMapping("/new")
    public ModelAndView addNewPatient(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                      ModelAndView modelAndView) {
        return patientService.saveNewPatient(patient, bindingResult, modelAndView);
    }

    @PostMapping("/confirmEmail/{id}")
    @PreAuthorize("#patient.id == authentication.principal.id and hasAuthority('USER')")
    @ResponseBody
    public String sendConfirmationEmail(@PathVariable("id") Patient patient) {
        patientService.sendConfirmationEmail(patient.getEmail(), patient.getName(), patient.getActivationCode());
        return "Успешно!\n Письмо с подтверждением было отправлено на почту!";
    }

    // remake for bind exception
    @PostMapping("/reset/{code}")
    public ModelAndView savePatientWithNewPassword(@PathVariable("code") String code, @RequestParam("password") String newPassword,
                                                   ModelAndView modelAndView, BindingResult bindingResult) {
        return patientService.saveResetPassword(code, newPassword, modelAndView, bindingResult);
    }

    @PostMapping("/send-deleteCode")
    @PreAuthorize("#patientEmail == authentication.principal.username and hasAuthority('USER')")
    @ResponseBody
    public String sendDeleteConfirmationMail(@RequestParam("patientEmail") String patientEmail,
                                             @RequestParam("confirmationCode") String confirmationCode) {
        System.out.println("hi");
        patientService.sendDeleteConfirmationMail(patientEmail, confirmationCode);
        return "Успешно!\n Письмо с кодом подтверждения удаления аккаунта было отправлено на почту!";
    }

    @PostMapping("/save-newPassword")
    @PreAuthorize("#id == authentication.principal.id and hasAuthority('USER')")
    @ResponseBody
    public String setNewPassword(@RequestParam("providedPassword") String providedPassword, @RequestParam("patientId") Long id) {
        return patientService.saveNewPassword(providedPassword, id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("(#patient.id == authentication.principal.id and hasAuthority('USER')) or hasAuthority('ADMIN')")
    public ModelAndView editPatientById(@ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResult,
                                        @RequestParam("profileImage") MultipartFile multipartFile, ModelAndView modelAndView) {
        return patientService.editPatient(patient, bindingResult, multipartFile, modelAndView);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(#id == authentication.principal.id and hasAuthority('USER')) or hasAuthority('ADMIN')")
    @ResponseBody
    public String deletePatientById(@PathVariable("id") Long id) {
        patientService.deletePatientById(id);
        return "Успешно!\n Аккаунт был удалён!";
    }

}
