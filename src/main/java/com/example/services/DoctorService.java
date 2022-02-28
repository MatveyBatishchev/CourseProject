package com.example.services;


import com.example.files.FileUploadUtil;
import com.example.models.Doctor;
import com.example.models.Role;
import com.example.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DoctorService {

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

    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    public void saveDoctorWithFile(Doctor doctor, MultipartFile multipartFile) {
        doctorRepository.save(doctor);
        if (!multipartFile.isEmpty()) {
            try {
                String uploadDir = uploadPath + "/doctors/" + doctor.getId();
                String fileName = UUID.randomUUID().toString() + "." + multipartFile.getContentType().substring(6);
                doctor.setImage(fileName);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            } catch (Exception e) {
                System.out.println("Ошибка в сохранении файла!");
            }
        }
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

}
