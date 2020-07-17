package com.halnode.atlantis.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "auth_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_name"),
        @UniqueConstraint(columnNames = "mobile_number"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name")
    @NotEmpty(message = "User name must not be null or empty")
    @Size(max = 32)
    private String userName;

    @Size(max = 72)
    private String password;

    @Transient
    private String confirmPassword;

    @Column(name = "first_name")
    @NotEmpty(message = "First name must not be null or empty")
    @Size(max = 32)
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 32)
    private String lastName;

    @Column(name = "mobile_number", unique = true)
    private Long mobileNumber;

    private String email;

    @Column(name = "country_code")
    private String countryCode;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Organization organization;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles;

    private boolean active;

    @Column(name = "is_admin")
    private Boolean isAdmin;
}
