package com.example.models;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Date;

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
    @Size(min=2, max=30, message = "Имя должно быть 2-х до 30 букв")
    private String name;
    @Size(min=2, max=30, message = "Фамилия должна быть 2-х до 30 букв")
    private String surname;
    private Date birthDate;
    private int sex;
    private String telephone;
    @Email(message = "Недопустимое значение")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$", message = "Пароль должен содержать минимум 8 символов разного регистра, минимум 1 цифру")
    private String password;

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

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, name='%s', surname='%s', birthDate='%s'" +
                        ", sex=%d, telephone='%s', email='%s', password='%s']",
                id, name, surname, birthDate, sex, telephone, email, password);
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
}
