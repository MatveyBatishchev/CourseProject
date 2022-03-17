package com.example.services;


import com.example.files.FileUploadUtil;
import com.example.models.Doctor;
import com.example.models.Patient;
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
    private MailSender mailSender;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, MailSender mailSender) {
        this.doctorRepository = doctorRepository;
        this.mailSender = mailSender;
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
        doctor.setRoles(Collections.singleton(Role.DOCTOR));
        if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) doctor.setPassword(generatePassword());
        sendPassword(doctor.getEmail(), doctor.getName(), doctor.getPassword());
        doctorRepository.save(doctor);
    }

    public void editDoctorWithFile(Doctor doctor, MultipartFile multipartFile) {
        try {
            String uploadDir = uploadPath + "/doctors/" + doctor.getId();
            String fileName = UUID.randomUUID().toString() + "." + multipartFile.getContentType().substring(6);
            doctor.setImage(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception e) {
            System.out.println("Ошибка в сохранении файла!");
        }
        doctor.setActive(true);
        doctor.setRoles(Collections.singleton(Role.DOCTOR));
        if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) doctor.setPassword(generatePassword());
        doctorRepository.save(doctor);
    }

    public void deleteDoctorById(Long id) {
        try {
            Doctor doctor = doctorRepository.findById(id).isPresent() ? doctorRepository.findById(id).get() : null;
            String doctorDir = uploadPath + "doctors/" + doctor.getId();
            FileUploadUtil.deleteDirectory(doctorDir);
        } catch (Exception e) {
            System.out.println("Ошибка в удалении файла доктора!");
        }
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

    public void sendPassword(String doctorEmail, String doctorName, String doctorPassword) {
        if (!doctorEmail.isEmpty()) {
            String message = String.format(
                    "Добро пожаловать в систему RecoveryMed, %s! \n" +
                            "Для входа в систему используете этот пароль: %s \n" +
                                "Сохраните пароль, это будет ваш единственный способ входа! По возможности удалите данное письмо. \n" +
                                    "С уважением команда RecoveryMed❤",
                    doctorName, doctorPassword
            );
            mailSender.send(doctorEmail,"Пароль к системе RecoveryMed", message);
        }
    }

}
