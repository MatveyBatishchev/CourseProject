package com.example.models;

import com.example.validation.ValidPassword;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Set;

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

    public Patient() {}

    public Patient(Long id, String name, String surname, Date birthDate,
                   int sex, String telephone, String email, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.sex = sex;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birth_date) {
        this.birthDate = birth_date;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
