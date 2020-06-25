package com.halnode.atlantis.product.persistence.repository;

import com.halnode.atlantis.product.persistence.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
