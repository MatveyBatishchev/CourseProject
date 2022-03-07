package com.example.services;

import com.example.models.*;
import com.example.repo.AppointmentRepository;
import com.example.repo.TimeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeTableRepository timeTableRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, TimeTableRepository timeTableRepository) {
        this.appointmentRepository = appointmentRepository;
        this.timeTableRepository = timeTableRepository;
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

    public void saveAppointment(Appointment appointment, Patient patient, Long timeTableId) {
        TimeTable timeTable = timeTableRepository.findById(timeTableId).get();
        appointment.setPatient(patient);
        appointment.setTime(timeTable.getStartTime());
        timeTable.setAvailable(false);
        timeTableRepository.save(timeTable);
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }



}
