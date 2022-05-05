package com.example.models;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Doctor implements User {

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
    @Expose
    private Long id;

    @Column(name="name", nullable=false)
    @Size(min=2, max=30, message = "Имя должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String name;

    @Column(name="surname", nullable=false)
    @Size(min=2, max=30, message = "Фамилия должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String surname;

    @Column(name="patronymic", nullable=false)
    @Size(min=2, max=30, message = "Отчество должно быть от 2-х до 30 букв")
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String patronymic;

    @Column(name="email", nullable=false)
    @Email(message = "Недопустимое значение")
    @NotBlank(message = "Это поле является обязательным")
    private String email;

    @Column(name="cabinet")
    @Expose
    private String cabinet;

    @Column(name="experience", nullable=false)
    @Min(value=1, message="Недопустимое значение")
    @Expose
    private int experience;

    @Column(name="category", nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    @Expose
    private String category;

    @Column(name="about_doctor", nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String aboutDoctor;

    @Column(name="education", nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String education;

    @Column(name="work_places", nullable=false)
    @NotBlank(message = "Это поле является обязательным")
    private String workPlaces;

    @Column(name="image")
    @Expose
    private String image;

    @Column(name="password",nullable=false)
    private String password;

    @Column(name="active", nullable=false)
    private boolean active;

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("date DESC")
    private Set<Review> reviews = new HashSet<>();

    @ElementCollection(targetClass = Speciality.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "doctor_speciality", joinColumns = @JoinColumn(name = "doctor_id"))
    @Enumerated(EnumType.STRING)
    @Expose
    private Set<Speciality> specialities;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_role", joinColumns = @JoinColumn(name = "doctor_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public String getProfileLink() {
        return "doctors/" + getId();
    }

    static final String[] declension = {"год", "года", "лет"};
    static final int[] cases = {2, 0, 1, 1, 1, 2};
    public String getExperienceWithPrefix() {
        int experience = getExperience();
        return experience + " " + declension[ (experience%100>4 &&experience%100<20)? 2 : cases[Math.min(experience % 10, 5)] ];
    }

    public List<String> getSpecialitiesLabels() {
        return specialities.stream().map(Speciality::toString).collect(Collectors.toList());
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
