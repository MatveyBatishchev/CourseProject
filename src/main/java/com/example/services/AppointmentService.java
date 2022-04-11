package com.example.services;

import com.example.files.PdfConverter;
import com.example.models.Appointment;
import com.example.models.Patient;
import com.example.models.TimeTable;
import com.example.repo.AppointmentRepository;
import com.example.repo.DoctorRepository;
import com.example.repo.TimeTableRepository;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TimeTableRepository timeTableRepository;
    private final PdfConverter pdfConverter;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository,
                              TimeTableRepository timeTableRepository, PdfConverter pdfConverter) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.timeTableRepository = timeTableRepository;
        this.pdfConverter = pdfConverter;
    }

    public List<Appointment> findAllAppointmentsAsc() {
        return appointmentRepository.findByOrderByIdAsc();
    }

    public Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public Long saveAppointment(Patient patient, String date, String time, Long doctorId, String callbackInfo) {
        Appointment appointment = new Appointment();

        // Date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsed = null;
        try {
            parsed = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Date sqlDate = new Date(parsed.getTime());
        appointment.setDate(sqlDate);

        // Time
        LocalTime localTime = LocalTime.parse(time + ":00");
        System.out.println(localTime);
        appointment.setTime(localTime);

        // Doctor
        appointment.setDoctor(doctorRepository.findById(doctorId).get());

        // Patient
        if (patient != null) {
            appointment.setPatient(patient);
            appointment.setStatus(0);
        }
        else appointment.setStatus(1);

        // TimeTable set isAvailable
        Long timeTableId = timeTableRepository.findTimeTableIdByDateAndTimeAbdDoctorId(sqlDate,localTime,doctorId);
        TimeTable timeTable = timeTableRepository.findById(timeTableId).get();
        timeTable.setAvailable(false);
        timeTableRepository.save(timeTable);

        if (!callbackInfo.isBlank()) appointment.setCallbackInfo(callbackInfo);

        appointmentRepository.save(appointment);

        return appointment.getId();
    }

    public void editAppointmentStatus(Long appointmentId, int status) {
        Appointment appointment = findAppointmentById(appointmentId);
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
            System.out.println("-----------------------");
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
