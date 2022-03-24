package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "user_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(name="date",nullable=false)
    private Date date;

    @Column(name="time",nullable=false)
    private LocalTime time;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name="call_info", nullable=true)
    String callbackInfo;

    @Column(name="status", nullable = false)
    private int status;

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
