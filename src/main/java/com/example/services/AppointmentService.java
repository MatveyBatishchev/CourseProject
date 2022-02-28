package com.example.services;

import com.example.models.Appointment;
import com.example.models.Patient;
import com.example.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findAllAppointmentsAsc() {
        return appointmentRepository.findByOrderByIdAsc();
    }

    public Appointment findAppointmentById(Long id) {
        Optional<Appointment> appointmentById = appointmentRepository.findById(id);
        return appointmentById.isPresent()? appointmentById.get() : null;
    }

    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public void saveAppointment(Appointment appointment, Patient patient, String time) {
        appointment.setPatient(patient);
        appointment.setTime(LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }



}
