package com.example.services;

import com.example.files.FileUploadUtil;
import com.example.mappers.PatientMapper;
import com.example.models.Patient;
import com.example.models.Role;
import com.example.repo.DoctorRepository;
import com.example.repo.PatientRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PatientService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MailSender mailSender;
    private final SmartValidator validator;
    private final PasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          DoctorRepository doctorRepository,
                          MailSender mailSender,
                          SmartValidator validator,
                          PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.mailSender = mailSender;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    public ModelAndView findAllPatients(ModelAndView modelAndView) {
        List<Patient> allPatients = patientRepository.findByOrderByIdAsc();
        modelAndView.addObject("patients", allPatients);
        modelAndView.setViewName("patients/getAll");
        return modelAndView;
    }

    public ModelAndView findAllPatientsWithPage(ModelAndView modelAndView) {
        Page<Patient> page = patientRepository.findAll(PageRequest.of(0, 2, Sort.by("id")));
        modelAndView.addObject("patients", page.getContent());
        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.setViewName("patients/getAll");
        return modelAndView;
    }

    public String findPatientsWithPage(Integer pageNumber) {
        Page<Patient> page = patientRepository.findAll(PageRequest.of(pageNumber, 2, Sort.by("id")));
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(page.getContent());
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

    public String findPatientBySearchWithPage(String search, Integer pageNumber) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject jsonObject = new JsonObject();
        String[] fullNameParts = search.split(" ");
        Pageable pageable = PageRequest.of(pageNumber, 1, Sort.by("id"));
        Page<Patient> page  = null;
        switch (fullNameParts.length) {
            case 0:
                return null;
            case 1:
                page = patientRepository.findPatientByOneString(fullNameParts[0], pageable);
                break;
            default:
                page = patientRepository.findPatientByTwoStrings(fullNameParts[0], fullNameParts[1], pageable);
                break;
        }
        jsonObject.addProperty("entities", gson.toJson(page.getContent()));
        jsonObject.addProperty("totalPages", page.getTotalPages());
        return jsonObject.toString();
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
        patient.setConfirmed(false);
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patient.setRoles(Collections.singleton(Role.USER));
        patient.setActivationCode(UUID.randomUUID().toString());
        patientRepository.save(patient);
        //sendConfirmationEmail(patient.getEmail(), patient.getName(), patient.getActivationCode());
    }

    // partial patient update
    public void updatePatient(Patient updatedPatient) {
        Patient patient = patientRepository.findById(updatedPatient.getId()).orElse(null);
        if (patient != null) {
            PatientMapper.INSTANCE.updatePatientFromUpdatedEntity(updatedPatient, patient);
            patientRepository.save(patient);
        }
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
            updatePatient(patient);
            modelAndView.setViewName("redirect:/patients/" + patient.getId());
        }
        return modelAndView;
    }

    public void deletePatientById(Long id) {
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
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        if (patientRepository.findByEmail(email) == null) {
            if (doctorRepository.findByEmail(email) == null) throw new UsernameNotFoundException("User was not found!");
            else return doctorRepository.findByEmail(email);
        }
        else return patientRepository.findByEmail(email);
    }

    public ModelAndView activatePatient(String code, ModelAndView modelAndView) {
        Patient patient = patientRepository.findByActivationCode(code);
        if (patient == null) modelAndView.addObject("message", "Код активации не был найден!");
        else {
            modelAndView.addObject("message", "Email успешно подтверждён!");
            patient.setActivationCode(null);
            patient.setConfirmed(true);
            patientRepository.save(patient);
        }
        modelAndView.setViewName("main/activationCode");
        return modelAndView;
    }

    public ModelAndView resetPassword(String code, ModelAndView modelAndView) {
        Patient patient = patientRepository.findByActivationCode(code);
        if (patient == null) {
            modelAndView.addObject("message", "Код активации не был найден!");
            modelAndView.setViewName("main/activationCode");
        }
        else {
            modelAndView.addObject("email", patient.getEmail());
            modelAndView.addObject("activationCode", code);
            modelAndView.setViewName("main/resetPassword");
        }
        return modelAndView;
    }

    public ModelAndView saveResetPassword(String code, String newPassword, ModelAndView modelAndView, BindingResult bindingResult) {
        Patient patient = patientRepository.findByActivationCode(code);
        if (patient == null) {
            modelAndView.addObject("message", "Произошла ошибка в процессе изменения пароля!");
            modelAndView.setViewName("main/activationCode");
        }
        else {
            patient.setPassword(newPassword);
            validator.validate(patient, bindingResult);
            if (bindingResult.hasErrors()) {
                modelAndView.addObject("passwordErrors", bindingResult.getAllErrors());
                modelAndView.addObject("email", patient.getEmail());
                modelAndView.addObject("activationCode", patient.getActivationCode());
                modelAndView.setViewName("main/resetPassword");
            }
            else {
                patient.setActivationCode(null);
                patient.setPassword(passwordEncoder.encode(patient.getPassword()));
                patientRepository.save(patient);
                modelAndView.addObject("email", patient.getEmail());
                modelAndView.setViewName("main/login");
            }
        }
        return modelAndView;
    }

    public String comparePasswords(String providedPassword, Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        String compare = "false";
        if (patient != null) {
            if (passwordEncoder.matches(providedPassword,patient.getPassword())) {
                compare = "true";
            }
        }
        return compare;
    }

    public String saveNewPassword(String providedPassword, Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient != null) {
            if (passwordEncoder.matches(providedPassword, patient.getPassword())) {
                return new Gson().toJson("same");
            }
            else {
                patient.setPassword(providedPassword);
                BindingResult bindingResult = new BindException(patient, "patient");
                validator.validate(patient, bindingResult);
                if (bindingResult.hasErrors()) {
                    String[] errors = bindingResult.getFieldError("password").getDefaultMessage().split(",");
                    return new Gson().toJson(errors);
                }
                else {
                   patient.setPassword(passwordEncoder.encode(patient.getPassword()));
                   patientRepository.save(patient);
                   return new Gson().toJson("success");
                }
                }
            }
        return new Gson().toJson("Произошла ошибка в процесссе изменения пароля! Повторите попытку позже.");
    }

    public void sendDeleteConfirmationMail(String patientEmail, String confirmationCode) {
        if (!patientEmail.isEmpty()) {
            String message = String.format(
                    """
                            Здравствуйте, с вашего аккаунта был отправлен запрос на удаление.\s
                            Никому не сообщайте код подтверждения! Если не вы отправляли запрос, обратитесь по телефону клиники.\s
                            Код подтверждения удаления аккаунта: %s\s
                            С уважением команда RecoveryMed❤""",
                    confirmationCode
            );
            mailSender.send(patientEmail,"Код для удаления аккаунта", message);
        }
    }

    public void sendConfirmationEmail(String patientEmail, String patientName, String activationCode) {
        if (activationCode == null || activationCode.isBlank()) {
            Patient patient = patientRepository.findByEmail(patientEmail);
            patient.setActivationCode(UUID.randomUUID().toString());
            patientRepository.save(patient);
        }
        if (!patientEmail.isEmpty()) {
            String message = String.format(
                    """
                            Добро пожаловать в RecoveryMed, %s!\s
                            Для подтверждения email-а, пожалуйста, перейдите по ссылке: https://localhost:443/patients/activate/%s\s
                            С уважением команда RecoveryMed❤""",
                    patientName, activationCode
            );
            mailSender.send(patientEmail,"Подтвердите электронную почту", message);
        }
    }

    public void sendResetPasswordEmail(String patientEmail) {
        Patient patient = patientRepository.findByEmail(patientEmail);
        if (patient != null) {
            String activationCode = UUID.randomUUID().toString();
            patient.setActivationCode(activationCode);
            patientRepository.save(patient);
            String message = String.format(
                    """
                            Привет, %s!\s
                            Для сброса вашего пароля и создания нового, пожалуйста, перейдите по ссылке: https://localhost:443/patients/reset/%s\s
                            С уважением команда RecoveryMed❤""",
                    patient.getName(), activationCode
            );
            mailSender.send(patientEmail,"Восстановление пароля", message);
        }
    }

    public String checkIfPatientExists(String email) {
        return new Gson().toJson(patientRepository.existsByEmail(email));
    }

    // erase all activation codes every day
    @Scheduled(cron = "0 0 12 * * *")
    public void resetActivationCodes() {
        for (Patient patient : patientRepository.findAll()) {
            patient.setActivationCode(null);
            patientRepository.save(patient);
        }
    }

}
