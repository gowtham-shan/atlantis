package com.halnode.atlantis.core.persistence.model;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Data
@Table(name = "organization", uniqueConstraints = {@UniqueConstraint(columnNames = "name")}, schema = "public")
@Audited
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long id;

    private String name;

//    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore
//    @NotAudited
//    private Set<User> users;


}
