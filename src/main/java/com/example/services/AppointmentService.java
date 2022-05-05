package com.example.services;

import com.example.models.Appointment;
import com.example.models.Patient;
import com.example.models.TimeTable;
import com.example.repo.AppointmentRepository;
import com.example.repo.DoctorRepository;
import com.example.repo.TimeTableRepository;
import com.example.util.PdfConverter;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TimeTableRepository timeTableRepository;
    private final PdfConverter pdfConverter;

    public Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Запись на приём с id " + id + "не была найдена!"));
    }

    public List<Appointment> findAllAppointmentsAsc() {
        return appointmentRepository.findByOrderByIdAsc();
    }

    public ModelAndView findAppointmentByIdView(Long id, ModelAndView modelAndView, String viewName) {
        Appointment appointmentById = findAppointmentById(id);
        modelAndView.addObject("appointment", appointmentById);
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    public Long saveAppointment(Patient patient, String date, String time, Long doctorId, String callbackInfo) {
        Appointment newAppointment = new Appointment();

        // Date
        java.util.Date parsed = null;
        try {
            parsed = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Date sqlDate = new Date(Objects.requireNonNull(parsed).getTime());
        newAppointment.setDate(sqlDate);

        // Time
        LocalTime localTime = LocalTime.parse(time + ":00");
        newAppointment.setTime(localTime);

        // Doctor
        newAppointment.setDoctor(doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Доктор с id " + doctorId + "не был найден!")));

        // Patient
        if (patient != null) {
            newAppointment.setPatient(patient);
            newAppointment.setStatus(0);
        }
        else newAppointment.setStatus(1);

        // TimeTable set isAvailable
        TimeTable timeTable = timeTableRepository.findTimeTableIdByDateAndTimeAndDoctorId(sqlDate,localTime,doctorId);
        timeTable.setAvailable(false);
        timeTableRepository.save(timeTable);

        if (!callbackInfo.isBlank()) newAppointment.setCallbackInfo(callbackInfo);

        appointmentRepository.save(newAppointment);

        return newAppointment.getId();
    }

    public void editAppointmentStatus(Long id, Integer status) {
        Appointment appointment = findAppointmentById(id);
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public ResponseEntity<byte[]> exportPdf(Long appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);
        try {
            pdfConverter.createPDF(appointment);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        byte[] contents = new byte[0];
        try {
            String fileName = "appointmentFiles/appointment" + appointment.getId() + ".pdf";
            contents = (Files.readAllBytes(new File(fileName).toPath()));
            new File(fileName).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "appointment" + appointment.getId() + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

}
