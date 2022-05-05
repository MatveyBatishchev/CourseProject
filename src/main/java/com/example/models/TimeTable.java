package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="time_table")
public class TimeTable implements Comparable<TimeTable> {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "user_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonIgnore
    Long id;

    @Column(name="start_time", nullable=false)
    @Expose
    private LocalTime startTime;

    @Column(name="date", nullable=false)
    @JsonIgnore
    private Date date;

    @Column(name="is_available", nullable=false)
    @JsonIgnore
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Schedule schedule;

    @Override
    public int compareTo(TimeTable o) {
        return  this.startTime.compareTo(o.startTime);
    }

}
