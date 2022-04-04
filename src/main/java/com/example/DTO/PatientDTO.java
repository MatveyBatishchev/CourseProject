package com.example.DTO;

import com.example.models.Appointment;
import com.example.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private Long id;
    private String name;
    private String surname;
    private Date birthDate;
    private int sex;
    private String telephone;
    private String email;
    private String password;
    private String image;
    private String activationCode;
    private boolean active;
    private boolean confirmed;
    private Set<Appointment> appointments = new HashSet<>();
    private Set<Role> roles;
}
