package com.example.services;


import com.example.mappers.DoctorMapper;
import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.models.Review;
import com.example.models.Role;
import com.example.repo.DoctorRepository;
import com.example.util.BindingResultSerializer;
import com.example.util.FileUploadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final SmartValidator validator;
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final int pageSize = 4;

    @Value("${upload.path}")
    private String uploadPath;

    public Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Доктор с id " + id + " не был найден!"));
    }

    public ModelAndView findAllDoctorsFirstPageView(ModelAndView modelAndView) {
        Page<Doctor> page = doctorRepository.findAll(PageRequest.of(0, pageSize, Sort.by("id")));
        modelAndView.addObject("doctors", page.getContent());
        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.setViewName("doctors/getAll");
        return modelAndView;
    }

    public ModelAndView findMainDoctorsFirstPageView(ModelAndView modelAndView) {
        Page<Doctor> page = findPageOfAllDoctors(0);
        Map<Integer, List<Doctor>> doctorMap = new HashMap<>();
        List<Doctor> doctors = new ArrayList<>();
        doctorMap.put(1, page.stream().limit(4).collect(Collectors.toList()));
        if (page.getNumberOfElements() > 4) {
            doctorMap.put(2, page.getContent().subList(4, page.getNumberOfElements()));
        }
        modelAndView.addObject("doctors", doctorMap);
        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.setViewName("main/doctors");
        return modelAndView;
    }

    public ModelAndView findDoctorByIdView(Long id, ModelAndView modelAndView, String viewName) {
        Doctor doctorById = findDoctorById(id);
        modelAndView.addObject("doctor", doctorById);
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    public ModelAndView findDoctorResumeByIdView(Long id, ModelAndView modelAndView) {
        Doctor doctorById = findDoctorById(id);
        modelAndView.addObject("doctor", doctorById);
        List<Review> reviews = new ArrayList<>();
        int count = 0;
        for (Review review : doctorById.getReviews()) {
            if (review.isApproved()) {
                if (count < 3) reviews.add(review);
                count++;
            }
        }
        modelAndView.addObject("doctorReviews", reviews);
        modelAndView.addObject("reviewsNumber", count);
        modelAndView.setViewName("doctors/resume");
        return modelAndView;
    }

    public Page<Doctor> findPageOfAllDoctors(Integer pageNumber) {
        return doctorRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
    }

    public Page<Doctor> findPageOfDoctorsBySearch(String search, Integer pageNumber) {
        String[] fullNameParts = search.split(" ");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        switch (fullNameParts.length) {
            case 0:
                return null;
            case 1:
                return doctorRepository.findDoctorByOneString(fullNameParts[0], pageable);
            case 2:
                return doctorRepository.findDoctorByTwoStrings(fullNameParts[0], fullNameParts[1], pageable);
            default:
                return doctorRepository.findDoctorByThreeStrings(fullNameParts[0], fullNameParts[1], fullNameParts[2], pageable);
        }
    }

    public String findDoctorsWithPageJson(Integer pageNumber) {
        Page<Doctor> page = doctorRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
        return gson.toJson(page.getContent());
    }

    public String findDoctorsBySearchWithPageJson(String search, Integer pageNumber) {
        JsonObject jsonObject = new JsonObject();
        Page<Doctor> page = findPageOfDoctorsBySearch(search, pageNumber);
        jsonObject.addProperty("entities", gson.toJson(page.getContent()));
        jsonObject.addProperty("totalPages", page.getTotalPages());
        return jsonObject.toString();
    }

    public String findDoctorByIdJson(Patient patient, Long id) {
        boolean reg = (patient != null);
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(findDoctorById(id));
        jsonElement.getAsJsonObject().addProperty("reg", reg);
        jsonObject.add("doctor", jsonElement);
        return jsonObject.toString();
    }

    public String findDoctorsBySpecialityJson(String speciality) {
        Date today = new Date();
        return gson.toJson(doctorRepository.findDoctorsBySpeciality(speciality)
                .stream()
                .filter(doctor -> doctor.getSchedules().stream()
                        .anyMatch(schedule -> schedule.getDate().after(today)))
                .collect(Collectors.toList()));
    }

    public String findDoctorsBySpecialityAndFullNameWithPageJson(String fullName, String speciality, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        JsonObject jsonObject = new JsonObject();
        Page<Doctor> page;
        if (fullName.isBlank() && speciality.isBlank()) {
            page = findPageOfAllDoctors(pageNumber);
        }
        else {
            if (!fullName.isBlank() && !speciality.isBlank()) {
                String[] fullNameParts = fullName.split(" ");
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
            }
            else {
                if (!speciality.isBlank()) {
                    page = doctorRepository.findDoctorsBySpeciality(speciality, pageable);
                }
                else {
                    page = findPageOfDoctorsBySearch(fullName, pageNumber);
                }
            }
        }
        jsonObject.addProperty("doctors", gson.toJson(Objects.requireNonNull(page).getContent()));
        jsonObject.addProperty("totalPages", page.getTotalPages());
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
//            String doctorPassword = generatePassword();
            String doctorPassword = "password";
            doctor.setPassword(passwordEncoder.encode(doctorPassword));
//            sendPassword(doctor.getEmail(), doctor.getName(), doctorPassword);
        }
        doctorRepository.save(doctor);
    }

    public String editDoctor(String doctorJson) {
        System.out.println(doctorJson);
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new SimpleModule().addSerializer(BindingResult.class,new BindingResultSerializer()));
        try {
            Doctor doctor = objMapper.readValue(doctorJson, Doctor.class);
            BindingResult bindingResult = new BindException(doctor, "doctor");
            validator.validate(doctor, bindingResult);
            if (bindingResult.hasErrors()) {
                return objMapper.writeValueAsString(bindingResult);
            }
            else {
                updateDoctor(doctor);
                return "";
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
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

    public void editDoctorResume(Long id, String aboutDoctor, String education, String workPlaces) {
        Doctor doctor = findDoctorById(id);
        doctor.setAboutDoctor(aboutDoctor);
        doctor.setEducation(education);
        doctor.setWorkPlaces(workPlaces);
        doctorRepository.save(doctor);
    }

    public void updateDoctor(Doctor updatedDoctor) {
        Doctor doctor = findDoctorById(updatedDoctor.getId());
        DoctorMapper.INSTANCE.updateDoctorFromUpdatedEntity(updatedDoctor, doctor);
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

    public void deleteDoctorById(Long id) {
        try {
            Doctor doctor = findDoctorById(id);
            String doctorDir = uploadPath + "doctors/" + doctor.getId();
            FileUploadUtil.deleteDirectory(doctorDir);
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
                    """
                            Добро пожаловать в систему RecoveryMed, %s!\s
                            Для входа в систему используете этот пароль: %s\s
                            Сохраните пароль, это будет ваш единственный способ входа! По возможности удалите данное письмо.\s
                            С уважением команда RecoveryMed❤""",
                    doctorName, doctorPassword
            );
            mailSender.send(doctorEmail,"Пароль к системе RecoveryMed", message);
        }
    }

}
