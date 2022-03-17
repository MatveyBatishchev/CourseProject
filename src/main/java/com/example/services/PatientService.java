package com.example.services;

import com.example.files.FileUploadUtil;
import com.example.models.Patient;
import com.example.models.Role;
import com.example.repo.DoctorRepository;
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
    private final DoctorRepository doctorRepository;
    private MailSender mailSender;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          DoctorRepository doctorRepository,
                          MailSender mailSender) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.mailSender = mailSender;
    }

    public void savePatient(Patient patient) {
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        patient.setActivationCode(UUID.randomUUID().toString());
        patientRepository.save(patient);
        //sendConfirmationEmail(patient.getEmail(), patient.getName(), patient.getActivationCode());
    }

    public void editPatient(Patient patient) {
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        if (patient.getActivationCode().isBlank()) patient.setActivationCode(null);
        patientRepository.save(patient);;
    }

    public void editPatientWithFile(Patient patient, MultipartFile multipartFile) {
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
        if (patient.getActivationCode().isBlank()) patient.setActivationCode(null);
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
        try {
            Patient patient = patientRepository.findById(id).isPresent() ? patientRepository.findById(id).get() : null;
            String patientDir = uploadPath + "patients/" + patient.getId();
            FileUploadUtil.deleteDirectory(patientDir);
        } catch (Exception e) {
            System.out.println("Ошибка в удалении файла пациента!");
        }
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
        if (patientRepository.findByEmail(email) == null) return doctorRepository.findByEmail(email);
        else return patientRepository.findByEmail(email);
    }

    public void sendConfirmationEmail(String patientEmail, String patientName, String activationCode) {
        if (!patientEmail.isEmpty()) {
            String message = String.format(
                    "Добро пожаловать в RecoveryMed, %s! \n" +
                            "Для подтверждения email-а, пожалуйста, перейдите по ссылке: http://localhost:8081/patients/activate/%s \n" +
                                "С уважением команда RecoveryMed❤",
                    patientName, activationCode
            );
            mailSender.send(patientEmail,"Подтвердите электронную почту", message);
        }
    }

    public boolean activateUser(String code) {
        Patient patient = patientRepository.findByActivationCode(code);

        if (patient == null) return false;

        patient.setActivationCode(null);
        patientRepository.save(patient);

        return true;
    }
}
