package com.example.controllers;

import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllDoctorsWithPageView(ModelAndView modelAndView) {
        return doctorService.findAllDoctorsFirstPageView(modelAndView);
    }

    @GetMapping("/all/page/{pageNumber}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public String getDoctorsWithPageJson(@PathVariable("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsWithPageJson(pageNumber);
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getNewDoctorView(@ModelAttribute("doctor") Doctor doctor) {
        return "doctors/newDoctor";
    }

    @GetMapping("/{id}")
    @PreAuthorize("(#id == authentication.principal.id and hasAuthority('DOCTOR')) or hasAuthority('ADMIN')")
    public ModelAndView getDoctorByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return doctorService.findDoctorByIdView(id, modelAndView, "doctors/getById");
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("(#id == authentication.principal.id and hasAuthority('DOCTOR')) or hasAuthority('ADMIN')")
    public ModelAndView getEditDoctorByIdView(@PathVariable("id") Long id, ModelAndView modelAndView) {
        return doctorService.findDoctorByIdView(id, modelAndView, "doctors/editById");
    }

    @GetMapping("/{id}/resume")
    public ModelAndView getDoctorResumeByIdView(@PathVariable("id") Long doctorId, ModelAndView modelAndView) {
        return doctorService.findDoctorResumeByIdView(doctorId, modelAndView);
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public String getDoctorByIdJson(@AuthenticationPrincipal Patient patient, @PathVariable("id") Long id) {
        return doctorService.findDoctorByIdJson(patient, id);
    }

    @GetMapping("/by-search")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public String getDoctorsBySearchWithPageJson(@RequestParam("search") String search,
                                                 @RequestParam("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsBySearchWithPageJson(search, pageNumber);
    }

    @GetMapping("/by-speciality")
    @ResponseBody
    public String getDoctorsBySpecialityJson(@RequestParam("speciality") String speciality) {
        return doctorService.findDoctorsBySpecialityJson(speciality);
    }

    @GetMapping("/by-expanded-search")
    @ResponseBody
    public String getDoctorsByExpandedSearchWithPageJson(@RequestParam("fullName") String fullName,
                                                         @RequestParam("speciality") String speciality,
                                                         @RequestParam("pageNumber") Integer pageNumber) {
        return doctorService.findDoctorsBySpecialityAndFullNameWithPageJson(fullName, speciality, pageNumber);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView addNewDoctor(@ModelAttribute("doctor") @Valid Doctor doctor,
                                     BindingResult bindingResult, ModelAndView modelAndView) {
        return doctorService.saveNewDoctor(doctor, bindingResult, modelAndView);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'DOCTOR'})")
    public ModelAndView editDoctorById(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult,
                                       @RequestParam("profileImage") MultipartFile multipartFile, ModelAndView modelAndView) {
        return doctorService.editDoctor(doctor, bindingResult, multipartFile, modelAndView);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'DOCTOR'})")
    public String deleteDoctorById(@PathVariable("id") Long id) {
        doctorService.deleteDoctorById(id);
        return "redirect:/doctors/all";
    }

}
