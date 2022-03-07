package com.example.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
public class Schedule {
    @Id
    @GeneratedValue
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "schedule_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(name="date",nullable=false)
    Date date;

    @Column(name="start_time",nullable=false)
    private LocalTime startTime;

    @Column(name="end_time",nullable=false)
    private LocalTime endTime;

    @Column(name="consulting_time",nullable=false)
    private Integer consultingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @OneToMany(
            mappedBy = "schedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<TimeTable> timeTable = new HashSet<>();

}
