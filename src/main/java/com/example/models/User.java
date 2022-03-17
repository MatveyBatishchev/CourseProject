package com.example.models;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public interface User extends UserDetails {

    String getEmail();
    String getPassword();
    String getProfileLink();

}
