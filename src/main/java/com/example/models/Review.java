package com.example.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

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

    @Column(name="creation_date", nullable=false)
    @Expose
    private Date date;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @Expose
    private Patient patient;

    @Column(name="comment", nullable=false)
    @Expose
    private String comment;

    @Column(name="approved", nullable=false)
    @Expose
    private boolean approved;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    public String getReviewerName() {
        return getPatient().getSurname() + " " + getPatient().getName();
    }
}
