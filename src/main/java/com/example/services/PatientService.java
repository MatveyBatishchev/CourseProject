package com.example.services;

import com.example.files.FileUploadUtil;
import com.example.models.Patient;
import com.example.models.Role;
import com.example.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class PatientService implements UserDetailsService {

    private final PatientRepository patientRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void savePatient(Patient patient) {
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        patientRepository.save(patient);
    }

    public void savePatientWithFile(Patient patient, MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            try {
                String uploadDir = uploadPath + "/patients/" + patient.getId();
                String fileName = UUID.randomUUID().toString() + "." + multipartFile.getContentType().substring(6);
                patient.setImage(fileName);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            } catch (Exception e) {
                System.out.println("Ошибка в сохранении файла!");
            }
        }
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        patientRepository.save(patient);
    }

    public List<Patient> findAllPatientsAsc() {
        return patientRepository.findByOrderByIdAsc();
    }

    public Patient findPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public Patient findPatientById(Long id) {
        Optional<Patient> patientById = patientRepository.findById(id);
        return patientById.isPresent()? patientById.get() : null;
    }

    public void deletePatientById(Long id) {
        patientRepository.deleteById(id);
    }

    public HashSet<Patient> searchPatientByString(String search) {
        List<Patient> patientsList = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            for (String s : search.split(" ")) {
                patientsList.addAll(patientRepository.findByNameIgnoreCaseOrSurnameIgnoreCase(s, s));
            }
        }
        else  patientsList = patientRepository.findAll();
        return new HashSet<>(patientsList);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return patientRepository.findByEmail(email);
    }
}
