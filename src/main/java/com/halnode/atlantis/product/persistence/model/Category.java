package com.halnode.atlantis.product.persistence.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "test_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private CategoryGroup categoryGroup;
}
