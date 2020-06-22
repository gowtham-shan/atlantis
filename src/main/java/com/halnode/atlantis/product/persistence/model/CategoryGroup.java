package com.halnode.atlantis.product.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "test_category_group")
public class CategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "categoryGroup")
    @JsonIgnore
    private Set<Category> categories;
}