package com.example.models;

import com.example.util.ValidPassword;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Patient implements User {
    @Id
    @SequenceGenerator(name="patient_generator", sequenceName="patient_sequence")
    @GeneratedValue(generator = "patient_generator")
    @Expose
    private Long id;

    @Column(name="name", nullable=false)
    @Size(min=2, max=30, message = "Имя должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String name;

    @Column(name="surname", nullable=false)
    @Size(min=2, max=30, message = "Фамилия должна быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String surname;

    @Column(name="birth_date", nullable=false)
    @NotNull(message = "Это поле является обязательным")
    @Expose
    private Date birthDate;

    @Column(name="sex", nullable=false)
    @Min(value=1, message="Недопустимое значение")
    @NotNull(message = "Это поле является обязательным")
    @Expose
    private int sex;

    @Column(name="telephone", nullable=false)
    @Size(min=17, max=18, message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String telephone;

    @Column(name="email", nullable=false)
    @Email(message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String email;

    @Column(name="password", nullable=false)
    @ValidPassword
    @NotBlank(message = "Это поле является обязательным")
    private String password;

    @Column(name="image")
    private String image;

    @Column(name="activation_code")
    private String activationCode;

    @Column(name="active", nullable=false)
    private boolean active;

    @Column(name="confirmed", nullable=false)
    private boolean confirmed;

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("date DESC")
    private Set<Review> reviews = new HashSet<>();

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "patient_role", joinColumns = @JoinColumn(name = "patient_id")) // описывает, что данное поле будет храниться в отдельной таблице, для кторой мы не описывали mapping
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public String getProfileLink() {
        return "patients/" + getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}
