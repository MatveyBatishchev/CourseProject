package com.example.services;


import com.example.files.FileUploadUtil;
import com.example.mappers.DoctorMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public ModelAndView findAllDoctors(ModelAndView modelAndView) {
        modelAndView.addObject("doctors", doctorRepository.findByOrderByIdAsc());
        modelAndView.setViewName("/doctors/getAll");
        return modelAndView;
    }

    public Page<Doctor> findAllDoctorsWithPage(Integer pageNumber) {
        return doctorRepository.findAll(PageRequest.of(pageNumber, 4, Sort.by("id")));
    }

    public ModelAndView findAllDoctorsWithPage(ModelAndView modelAndView) {
        Page<Doctor> page = doctorRepository.findAll(PageRequest.of(0, 2, Sort.by("id")));
        modelAndView.addObject("doctors", page.getContent());
        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.setViewName("doctors/getAll");
        return modelAndView;
    }

    public String findDoctorsWithPage(Integer pageNumber) {
        Page<Doctor> page = doctorRepository.findAll(PageRequest.of(pageNumber, 2, Sort.by("id")));
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(page.getContent());
    }

    public String findDoctorBySearchWithPage(String search, Integer pageNumber) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject jsonObject = new JsonObject();
        String[] fullNameParts = search.split(" ");
        Pageable pageable = PageRequest.of(pageNumber, 1, Sort.by("id"));
        Page<Doctor> page  = null;
        switch (fullNameParts.length) {
            case 0:
                return null;
            case 1:
                page = doctorRepository.findDoctorByOneString(fullNameParts[0], pageable);
                break;
            case 2:
                page = doctorRepository.findDoctorByTwoStrings(fullNameParts[0], fullNameParts[1], pageable);
                break;
            default:
                page = doctorRepository.findDoctorByThreeStrings(fullNameParts[0], fullNameParts[1], fullNameParts[2], pageable);
                break;
        }
        jsonObject.addProperty("entities", gson.toJson(page.getContent()));
        jsonObject.addProperty("totalPages", page.getTotalPages());
        return jsonObject.toString();
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
        Date today = new Date();
        return gson.toJson(doctorRepository.findDoctorsBySpeciality(speciality)
                .stream()
                .filter(doctor -> doctor.getSchedules().stream().anyMatch(schedule ->
                        schedule.getDate().after(today)))
                .collect(Collectors.toList()));
    }

    public ModelAndView findDoctorResumeById(Long doctorId, ModelAndView modelAndView) {
        Doctor doctorById = doctorRepository.findById(doctorId).orElse(null);
        if (doctorById == null) {
            modelAndView.addObject("object", "Доктор");
            modelAndView.setViewName("mistakes/notFound");
        }
        else {
            modelAndView.addObject("doctor", doctorById);
            modelAndView.setViewName("doctors/resume");
        }
        return modelAndView;
    }

    public String findDoctorsBySpecialityAndFullNameWithPage(String fullName, String speciality, Integer pageNumber) {
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject jsonObject = new JsonObject();
        if (fullName.isBlank() && speciality.isBlank()) {
            Page<Doctor> page = findAllDoctorsWithPage(pageNumber);
            jsonObject.addProperty("doctors", gson.toJson(page.getContent()));
            jsonObject.addProperty("totalPages", page.getTotalPages());
        }
        else {
            if (!fullName.isBlank() && !speciality.isBlank()) {
                String[] fullNameParts = fullName.split(" ");
                Page<Doctor> page = null;
                switch (fullNameParts.length) {
                    case 1:
                        page = doctorRepository.findDoctorBySpecialityAndOneString(speciality, fullNameParts[0], pageable);
                        break;
                    case 2:
                        page = doctorRepository.findDoctorBySpecialityAndTwoStrings(speciality, fullNameParts[0], fullNameParts[1], pageable);
                        break;
                    default:
                        page = doctorRepository.findDoctorBySpecialityAndThreeStrings(speciality, fullNameParts[0], fullNameParts[1], fullNameParts[2], pageable);
                        break;
                }
                jsonObject.addProperty("doctors", gson.toJson(page.getContent()));
                jsonObject.addProperty("totalPages", page.getTotalPages());
            }
            else {
                if (!speciality.isBlank()) {
                    Page<Doctor> page = doctorRepository.findDoctorsBySpeciality(speciality, pageable);
                    jsonObject.addProperty("doctors", gson.toJson(page.getContent()));
                    jsonObject.addProperty("totalPages", page.getTotalPages());
                }
                else {
                    String[] fullNameParts = fullName.split(" ");
                    Page<Doctor> page = null;
                    Arrays.stream(fullNameParts).forEach(System.out::println);
                    switch (fullNameParts.length) {
                        case 1:
                            page = doctorRepository.findDoctorByOneString(fullNameParts[0], pageable);
                            break;
                        case 2:
                            page = doctorRepository.findDoctorByTwoStrings(fullNameParts[0], fullNameParts[1], pageable);
                            break;
                        default:
                            page = doctorRepository.findDoctorByThreeStrings(fullNameParts[0], fullNameParts[1], fullNameParts[2], pageable);
                            break;
                    }
                    jsonObject.addProperty("doctors", gson.toJson(page.getContent()));
                    jsonObject.addProperty("totalPages", page.getTotalPages());
                }
            }
        }
        return jsonObject.toString();
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
            String doctorPassword = generatePassword();
            doctor.setPassword(passwordEncoder.encode(doctorPassword));
            System.out.println(doctorPassword);
            //sendPassword(doctor.getEmail(), doctor.getName(), doctorPassword);
        }
        doctorRepository.save(doctor);
    }

    public void updateDoctor(Doctor updatedDoctor) {
        Doctor doctor = doctorRepository.findById(updatedDoctor.getId()).orElse(null);
        if (doctor != null) {
            DoctorMapper.INSTANCE.updateDoctorFromUpdatedEntity(updatedDoctor, doctor);
            doctorRepository.save(doctor);
        }
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
            updateDoctor(doctor);
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
