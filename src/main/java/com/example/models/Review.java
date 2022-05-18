package com.example.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @SequenceGenerator(name="review_generator", sequenceName="review_sequence")
    @GeneratedValue(generator = "review_generator")
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
