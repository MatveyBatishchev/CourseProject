package com.example.services;


import com.example.files.FileUploadUtil;
import com.example.models.Doctor;
import com.example.models.Role;
import com.example.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DoctorService implements UserDetailsService {

    private final DoctorRepository doctorRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> findAllDoctorsAsc() {
        return doctorRepository.findByOrderByIdAsc();
    }

    public Doctor findDoctorById(Long id) {
        Optional<Doctor> doctorById = doctorRepository.findById(id);
        return doctorById.isPresent()? doctorById.get() : null;
    }

    public Doctor findDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    public void saveDoctor(Doctor doctor) {
        doctor.setActive(true);
        doctor.setRoles(Collections.singleton(Role.USER));
        System.out.println(doctor.getPassword());
        if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) doctor.setPassword(generatePassword());
        doctorRepository.save(doctor);
    }

    public void saveDoctorWithFile(Doctor doctor, MultipartFile multipartFile) {
        try {
            String uploadDir = uploadPath + "/doctors/" + doctor.getId();
            String fileName = UUID.randomUUID().toString() + "." + multipartFile.getContentType().substring(6);
            doctor.setImage(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception e) {
            System.out.println("Ошибка в сохранении файла!");
        }
        doctor.setActive(true);
        doctor.setRoles(Collections.singleton(Role.USER));
        if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) doctor.setPassword(generatePassword());
        doctorRepository.save(doctor);
    }

    public void deleteDoctorById(Long id) {
        doctorRepository.deleteById(id);
    }

    public HashSet<Doctor> searchDoctorsByString(String search) {
        List<Doctor> doctorsList = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            for (String s : search.split(" ")) {
                doctorsList.addAll(doctorRepository.findByNameIgnoreCaseOrSurnameIgnoreCaseOrPatronymicIgnoreCase(s, s, s));
            }
        }
        else doctorsList = doctorRepository.findAll();
        return new HashSet<>(doctorsList);
    }

    public List<Doctor> findDoctorsBySpeciality(String speciality) {
        return doctorRepository.findDoctorsBySpeciality(speciality);
    }

    public String generatePassword() {
        String genPassword = "";
        String symbols = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 12; i++) {
            genPassword += symbols.charAt((int) Math.round(Math.random() * symbols.length()));
        }
        return genPassword;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return doctorRepository.findByEmail(email);
    }
}
