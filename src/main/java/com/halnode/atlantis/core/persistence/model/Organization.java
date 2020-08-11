package com.halnode.atlantis.core.persistence.model;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@Table(name = "organization", uniqueConstraints = {@UniqueConstraint(columnNames = "name")}, schema = "public")
@Audited
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long id;

    @NotEmpty(message = "Organization name must not be null or empty")
    @Column(nullable = false,length = 128)
    private String name;
    
}
