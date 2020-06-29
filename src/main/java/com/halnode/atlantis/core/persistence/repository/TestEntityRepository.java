package com.halnode.atlantis.core.persistence.repository;

import com.halnode.atlantis.core.persistence.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityRepository extends JpaRepository<TestEntity, String> {
}
