package com.example.services;


import com.example.files.FileUploadUtil;
import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.models.Role;
import com.example.repo.DoctorRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final MailSender mailSender;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, MailSender mailSender) {
        this.doctorRepository = doctorRepository;
        this.mailSender = mailSender;
    }

    public ModelAndView findAllDoctors(ModelAndView modelAndView) {
        modelAndView.addObject("doctors", doctorRepository.findByOrderByIdAsc());
        modelAndView.setViewName("/doctors/getAll");
        return modelAndView;
    }

    public ModelAndView findDoctorById(Long doctorId, ModelAndView modelAndView) {
        Doctor doctorById = doctorRepository.findById(doctorId).orElse(null);
        if (doctorById == null) {
            modelAndView.addObject("object", "Доктор");
            modelAndView.setViewName("mistakes/notFound");
        }
        else {
            modelAndView.addObject("doctor", doctorById);
            modelAndView.setViewName("doctors/getById");
        }
        return modelAndView;
    }

    public ModelAndView findDoctorByIdForEdit(Long doctorId, ModelAndView modelAndView) {
        Doctor doctorById = doctorRepository.findById(doctorId).orElse(null);
        if (doctorById == null) {
            modelAndView.addObject("object", "Доктор");
            modelAndView.setViewName("mistakes/notFound");
        }
        else {
            modelAndView.addObject("doctor", doctorById);
            modelAndView.setViewName("doctors/editById"); }
        return modelAndView;
    }

    public String findDoctorByIdJson(Patient patient, Long doctorId) {
        boolean reg = true;
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(doctorRepository.findById(doctorId).orElse(null));
        if (patient == null) reg = false;
        jsonElement.getAsJsonObject().addProperty("reg", reg);
        jsonObject.add("doctor", jsonElement);
        return jsonObject.toString();
    }

    public String findDoctorsBySpeciality(String speciality) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(doctorRepository.findDoctorsBySpeciality(speciality));
    }

    public HashSet<Doctor> findDoctorsBySearch(String search) {
        List<Doctor> doctorsList = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            for (String s : search.split(" ")) {
                doctorsList.addAll(doctorRepository.findByNameIgnoreCaseOrSurnameIgnoreCaseOrPatronymicIgnoreCase(s, s, s));
            }
        }
        else doctorsList = doctorRepository.findAll();
        return new HashSet<>(doctorsList);
    }

    public ModelAndView saveNewDoctor(Doctor doctor, BindingResult bindingResult, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("doctors/newDoctor");
            return modelAndView;
        }
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            modelAndView.addObject("emailMessage", "Специалист с таким email уже существует!");
            modelAndView.setViewName("doctors/newDoctor");
            return modelAndView;
        }
        saveDoctor(doctor);
        modelAndView.setViewName("redirect:/doctors/all");
        return modelAndView;
    }

    public void saveDoctor(Doctor doctor) {
        doctor.setActive(true);
        doctor.setRoles(Collections.singleton(Role.DOCTOR));
        if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) {
            doctor.setPassword(generatePassword());
            sendPassword(doctor.getEmail(), doctor.getName(), doctor.getPassword());
        }
        doctorRepository.save(doctor);
    }

    public void saveDoctorFile(Doctor doctor, MultipartFile multipartFile) {
        try {
            String uploadDir = uploadPath + "/doctors/" + doctor.getId();
            String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(multipartFile.getContentType()).substring(6);
            doctor.setImage(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception e) {
            System.out.println("Ошибка в сохранении файла!");
        }
    }

    public ModelAndView editDoctor(Doctor doctor, BindingResult bindingResult, MultipartFile multipartFile, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("doctors/editById");
        }
        else {
            if (!multipartFile.isEmpty()) saveDoctorFile(doctor, multipartFile);
            saveDoctor(doctor);
            modelAndView.setViewName("redirect:/doctors/" + doctor.getId());
        }
        return modelAndView;
    }

    public void deleteDoctorById(Long id) {
        try {
            Doctor doctor = doctorRepository.findById(id).orElse(null);
            if (doctor != null) {
                String doctorDir = uploadPath + "doctors/" + doctor.getId();
                FileUploadUtil.deleteDirectory(doctorDir);
            }
            else throw new Exception("Doctor for delete operation appeared to be null!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        doctorRepository.deleteById(id);
    }

    public String generatePassword() {
        StringBuilder genPassword = new StringBuilder();
        String symbols = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 12; i++) {
            genPassword.append(symbols.charAt((int) Math.round(Math.random() * symbols.length())));
        }
        return genPassword.toString();
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
