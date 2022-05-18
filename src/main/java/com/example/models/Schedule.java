package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer","handler"})
public class Schedule {
    @Id
    @SequenceGenerator(name="schedule_generator", sequenceName="schedule_sequence")
    @GeneratedValue(generator = "schedule_generator")
    @Expose
    private Long id;

    @Column(name="date",nullable=false)
    @Expose
    Date date;

    @Column(name="start_time",nullable=false)
    private LocalTime startTime;

    @Column(name="end_time",nullable=false)
    private LocalTime endTime;

    @Column(name="consulting_time",nullable=false)
    private Integer consultingTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @JsonIgnore
    @OneToMany(
            mappedBy = "schedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<TimeTable> timeTable = new HashSet<>();

}
