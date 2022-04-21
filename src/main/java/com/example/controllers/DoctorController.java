package com.example.controllers;

import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getAllDoctors(ModelAndView modelAndView) {
        return doctorService.findAllDoctors(modelAndView);
    }

    @GetMapping("/{id}")
    public ModelAndView getDoctorById(@PathVariable("id") Long doctorId, ModelAndView modelAndView) {
        return doctorService.findDoctorById(doctorId, modelAndView);
    }

    @GetMapping("/getById")
    @ResponseBody
    public String getDoctorByIdJson(@AuthenticationPrincipal Patient patient, @RequestParam("doctorId") Long doctorId) {
        return doctorService.findDoctorByIdJson(patient, doctorId);
    }

    @GetMapping("/by_speciality")
    @ResponseBody
    public String getDoctorsBySpeciality(@RequestParam("speciality") String speciality) {
        return doctorService.findDoctorsBySpeciality(speciality);
    }

    @GetMapping("/search")
    public String getDoctorBySearch(@RequestParam("search") String search, Model model) {
        model.addAttribute("doctors", doctorService.findDoctorsBySearch(search));
        return "/doctors/getAll";
    }

    @GetMapping("/find")
    @ResponseBody
    public String findDoctorBySearch(@RequestParam("fullName") String fullName,
                                     @RequestParam("speciality") String speciality,
                                     @RequestParam("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsBySpecialityAndFullNameWithPage(fullName, speciality, pageNumber);
    }

    @GetMapping("/new")
    public String addNewDoctor(@ModelAttribute("doctor") Doctor doctor) {
        return "doctors/newDoctor";
    }

    @PostMapping("/new")
    public ModelAndView addNewDoctor(@ModelAttribute("doctor") @Valid Doctor doctor,
                                     BindingResult bindingResult, ModelAndView modelAndView) {
        return doctorService.saveNewDoctor(doctor, bindingResult, modelAndView);
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editDoctorById(@PathVariable("id") Long doctorId, ModelAndView modelAndView) {
        return doctorService.findDoctorByIdForEdit(doctorId, modelAndView);
    }

    @PutMapping("/{id}")
    public ModelAndView editDoctorById(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult,
                                       @RequestParam("profileImage") MultipartFile multipartFile, ModelAndView modelAndView) {
        return doctorService.editDoctor(doctor, bindingResult, multipartFile, modelAndView);
    }

    @DeleteMapping("/{id}")
    public String deletePatientById(@PathVariable("id") Long id) {
        doctorService.deleteDoctorById(id);
        return "redirect:/doctors/all";
    }

}
