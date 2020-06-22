package com.halnode.atlantis.core.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "organization",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users;


}
