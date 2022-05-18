package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {

    @Id
    @SequenceGenerator(name="appointment_generator", sequenceName="appointment_sequence")
    @GeneratedValue(generator = "appointment_generator")
    private Long id;

    @Column(name="date",nullable=false)
    private Date date;

    @Column(name="time",nullable=false)
    private LocalTime time;

    @Column(name="call_info")
    String callbackInfo;

    @Column(name="status", nullable = false)
    private int status;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    public String getPatientName() {
        return patient != null ? patient.getSurname() + " " + patient.getName() : callbackInfo.substring(0,callbackInfo.indexOf(" ", callbackInfo.indexOf(" ") + 1));
    }

    public String getPatientTelephone() {
        return patient != null ? patient.getTelephone() : callbackInfo.substring(callbackInfo.indexOf(" ", callbackInfo.indexOf(" ") + 1));
    }

}
