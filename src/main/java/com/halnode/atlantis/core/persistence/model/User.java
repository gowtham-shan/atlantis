package com.halnode.atlantis.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@Table(name = "auth_user", schema = "public")
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

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "country_code")
    private String countryCode;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Organization organization;

    @ManyToMany
    @JoinTable(
            name = "user_groups",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}, schema = "public")
    private Collection<Group> groups;

    private boolean active;

    @Column(name = "is_admin")
    private boolean isAdmin;
}
