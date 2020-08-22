package com.halnode.atlantis.core.persistence.model;

import com.halnode.atlantis.core.annotations.validation.ValidMobileNumber;
import com.halnode.atlantis.core.constants.UserType;
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

    @Column(name = "name")
    @NotEmpty(message = "Name should not be null or empty")
    @Size(max = 32)
    private String name;

    @Column(name = "mobile_number", unique = true)
    @ValidMobileNumber
    private Long mobileNumber;

    private String email;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Organization organization;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles;

    private boolean active;

    @Column(name="user_type")
    private UserType userType;
}
