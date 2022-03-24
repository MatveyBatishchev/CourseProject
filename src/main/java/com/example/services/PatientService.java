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
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Service
public class PatientService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MailSender mailSender;

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

    public ModelAndView findAllPatients(ModelAndView modelAndView) {
        modelAndView.addObject("patients",patientRepository.findByOrderByIdAsc());
        modelAndView.setViewName("patients/getAll");
        return modelAndView;
    }

    public ModelAndView findPatientById(Long patientId, ModelAndView modelAndView) {
        Patient patientById = patientRepository.findById(patientId).orElse(null);
        if (patientById == null) {
            modelAndView.addObject("object", "Пациент");
            modelAndView.setViewName("mistakes/notFound");
        }
        else {
            modelAndView.addObject("patient", patientById);
            modelAndView.setViewName("patients/getById");
        }
        return modelAndView;
    }

    public ModelAndView findPatientByIdForEdit(Long patientId, ModelAndView modelAndView) {
        Patient patientById = patientRepository.findById(patientId).orElse(null);
        if (patientById == null) {
            modelAndView.addObject("object", "Пациент");
            modelAndView.setViewName("mistakes/notFound");
        }
        else {
            modelAndView.addObject("patient", patientById);
            modelAndView.setViewName("patients/editById");
        }
        return modelAndView;
    }

    public HashSet<Patient> findPatientBySearch(String search) {
        List<Patient> patientsList = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            for (String s : search.split(" ")) {
                patientsList.addAll(patientRepository.findByNameIgnoreCaseOrSurnameIgnoreCase(s, s));
            }
        }
        else  patientsList = patientRepository.findAll();
        return new HashSet<>(patientsList);
    }

    public ModelAndView saveNewPatient(Patient patient, BindingResult bindingResult, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("main/registration");
        }
        else {
            if (patientRepository.findByEmail(patient.getEmail()) != null) {
                modelAndView.addObject("emailMessage", "Пользователь с таким email уже существует!");
                modelAndView.setViewName("main/registration");
            }
            else {
                savePatientWithEmail(patient);
                modelAndView.addObject("email", patient.getEmail());
                modelAndView.setViewName("main/login");
            }
        }
        return modelAndView;
    }

    public void savePatientWithEmail(Patient patient) {
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        patient.setActivationCode(UUID.randomUUID().toString());
        patientRepository.save(patient);
        sendConfirmationEmail(patient.getEmail(), patient.getName(), patient.getActivationCode());
    }

    public void savePatient(Patient patient) {
        patient.setActive(true);
        patient.setRoles(Collections.singleton(Role.USER));
        if (patient.getActivationCode().isBlank()) patient.setActivationCode(null);
        patientRepository.save(patient);
    }

    public void savePatientFile(Patient patient, MultipartFile multipartFile) {
        try {
            String uploadDir = uploadPath + "/patients/" + patient.getId();
            String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(multipartFile.getContentType()).substring(6);
            patient.setImage(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception e) {
            System.out.println("Ошибка в сохранении файла!");
        }
    }

    public ModelAndView editPatient(Patient patient, BindingResult bindingResult, MultipartFile multipartFile, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("/patients/editById");
        }
        else {
            if (!multipartFile.isEmpty()) savePatientFile(patient, multipartFile);
            savePatient(patient);
            modelAndView.setViewName("redirect:/patients/" + patient.getId());
        }
        return modelAndView;
    }

    public ModelAndView deletePatientById(Long id, ModelAndView modelAndView) {
        try {
            Patient patient = patientRepository.findById(id).orElse(null);
            if (patient != null) {
                String patientDir = uploadPath + "patients/" + patient.getId();
                FileUploadUtil.deleteDirectory(patientDir);
            }
        } catch (Exception e) {
            System.out.println("Ошибка в удалении файла пациента!");
        }
        patientRepository.deleteById(id);
        modelAndView.setViewName("redirect:/patients/all");
        return modelAndView;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (patientRepository.findByEmail(email) == null) return doctorRepository.findByEmail(email);
        else return patientRepository.findByEmail(email);
    }

    public ModelAndView activatePatient(String code, ModelAndView modelAndView) {
        Patient patient = patientRepository.findByActivationCode(code);
        if (patient == null) modelAndView.addObject("activationMessage", "Код активации не был найден!");
        else {
            modelAndView.addObject("activationMessage", "Email успешно подтверждён!");
            patient.setActivationCode(null);
            patientRepository.save(patient);
        }
        modelAndView.setViewName("main/activationCode");
        return modelAndView;
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
}
