package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Doctor {
    @Id
    @GeneratedValue
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

    @Column(name="name",nullable=false)
    @Size(min=2, max=30, message = "Имя должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String name;

    @Column(name="surname",nullable=false)
    @Size(min=2, max=30, message = "Фамилия должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String surname;

    @Column(name="patronymic",nullable=false)
    @Size(min=2, max=30, message = "Отчество должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String patronymic;

    @Column(name="experience",nullable=false)
    @Min(value=1, message="Недопустимое значение")
    private int experience;

    @Column(name="category",nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String category;

    @Column(name="aboutDoctor",nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String aboutDoctor;

    @Column(name="skills",nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String skills;

    @Column(name="workPlaces",nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String workPlaces;

    @Column(name="image",nullable=true)
    private String image;

    @ElementCollection(targetClass = Speciality.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_speciality", joinColumns = @JoinColumn(name = "doctor_id")) // описывает, что данное поле будет храниться в отдельной таблице, для которой мы не описывали mapping
    @Enumerated(EnumType.STRING)
    private Set<Speciality> specialities;

}
