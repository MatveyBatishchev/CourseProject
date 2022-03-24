package com.example.models;

import com.example.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
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

    @Column(name="name", nullable=false)
    @Size(min=2, max=30, message = "Имя должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String name;

    @Column(name="surname", nullable=false)
    @Size(min=2, max=30, message = "Фамилия должна быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    private String surname;

    @Column(name="birth_date", nullable=false)
    @NotNull(message = "Это поле является обязательным")
    private Date birthDate;

    @Column(name="sex", nullable=false)
    @Min(value=1, message="Недопустимое значение")
    @NotNull(message = "Это поле является обязательным")
    private int sex;

    @Column(name="telephone", nullable=false)
    @Size(min=17, max=18)
    @NotBlank(message = "Это поле является обязательным")
    private String telephone;

    @Column(name="email", nullable=false)
    @Email(message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    private String email;

    @Column(name="password", nullable=false)
    @ValidPassword
    @NotBlank(message = "Это поле является обязательным")
    private String password;

    @Column(name="image", nullable=true)
    private String image;

    @Column(name="activation_code", nullable=true)
    private String activationCode;

    @Column(name="active", nullable=false)
    private boolean active;

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<Appointment> appointments = new HashSet<>();

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
