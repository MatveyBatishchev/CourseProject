package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;

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

    public String getPatientName() {
        return patient != null ? patient.getName() : "<none>";
    }

    public String getPatientEmail() {
        return patient != null ? patient.getEmail() : "<none>";
    }

}
