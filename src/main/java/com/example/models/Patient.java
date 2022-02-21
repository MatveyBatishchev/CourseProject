package com.example.models;

import com.example.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Patient {
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

    @Size(min=2, max=30, message = "Имя должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String name;

    @Size(min=2, max=30, message = "Фамилия должна быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String surname;

    @NotNull(message = "Это поле является обязательным")
    private Date birthDate;

    @Min(value=1, message="Недопустимое значение")
    @NotNull(message = "Это поле является обязательным")
    private int sex;

    @Size(min=17, max=18)
    @NotBlank(message = "Это поле является обязательным")
    private String telephone;

    @Email(message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    private String email;

    @ValidPassword
    @NotBlank(message = "Это поле является обязательным")
    private String password;

    private boolean active;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "patient_role", joinColumns = @JoinColumn(name = "patient_id")) // описывает, что данное поле будет храниться в отдельной таблице, для кторой мы не описывали mapping
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

}
