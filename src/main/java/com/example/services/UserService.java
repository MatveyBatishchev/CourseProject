package com.example.services;

import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.repo.DoctorRepository;
import com.example.repo.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Patient patient = patientRepository.findByEmail(email);
        Doctor doctor = doctorRepository.findByEmail(email);
        if (patient == null) {
            if (doctor == null) throw new UsernameNotFoundException("User was not found!");
            else return doctor;
        }
        else return patient;
    }

}
