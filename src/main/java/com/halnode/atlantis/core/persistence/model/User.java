package com.halnode.atlantis.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "auth_user", schema = "public", uniqueConstraints = {
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
    private String userName;

    private String password;

    @Transient
    private String confirmPassword;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // mobile number is used as username by spring security
    @Column(name = "mobile_number")
    private Long mobileNumber;

    private String email;

    @Column(name = "country_code")
    private String countryCode;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Organization organization;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "auth_user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}, schema = "public")
    private Set<Role> roles;

    private boolean active;

    @Column(name = "is_admin")
    private Boolean isAdmin;
}
