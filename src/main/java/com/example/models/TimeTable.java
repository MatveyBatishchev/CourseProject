package com.example.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="time_table")
public class TimeTable {
    @Id
    @GeneratedValue
    Long id;

    @Column(name="start_time", nullable=false)
    private LocalTime startTime;

    @Column(name="date", nullable=false)
    private Date date;

    @Column(name="is_available", nullable=false)
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;


}
