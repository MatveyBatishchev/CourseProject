package com.example.controllers;

import com.example.models.Appointment;
import com.example.models.Doctor;
import com.example.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
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
            return "mistakes/notFound";
        }
        model.addAttribute("doctor", doctorById);
        return "doctors/getById";
    }

    @GetMapping("/new")
    public String addNewDoctor(@ModelAttribute("doctor") Doctor doctor) {
        return "doctors/newDoctor";
    }

    @PostMapping("/new")
    public String addNewDoctor(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "doctors/newDoctor";

        if (doctorService.findDoctorByEmail(doctor.getEmail()) != null) {
            model.addAttribute("emailMessage", "Специалист с таким email уже существует!");
            return "doctors/newDoctor";
        }

        doctorService.saveDoctor(doctor);
        return "redirect:/doctors/all";
    }

    @GetMapping("/{id}/edit")
    public String editDoctorById( @PathVariable("id") Long id, Model model) {
        Doctor doctorById = doctorService.findDoctorById(id);
        if (doctorById == null) {
            model.addAttribute("object", "Доктор");
            return "mistakes/notFound";
        }
        model.addAttribute("doctor", doctorById);
        return "doctors/editById";
    }

    @PutMapping("/{id}")
    public String editDoctorById(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult,
                                 @RequestParam("profileImage") MultipartFile multipartFile) {
        if (bindingResult.hasErrors()) return "doctors/editById";
        if (multipartFile.isEmpty()) doctorService.saveDoctor(doctor);
        else doctorService.saveDoctorWithFile(doctor, multipartFile);
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

    @GetMapping("/by_speciality")
    public String getDoctorsBySpeciality(Model model,
                                         @ModelAttribute("appointment") Appointment appointment,
                                         @RequestParam("speciality") String speciality) {
        model.addAttribute("doctors", doctorService.findDoctorsBySpeciality(speciality));
        model.addAttribute("selectedSpec", speciality);
        return "appointments/newAppointment2";
    }

}
