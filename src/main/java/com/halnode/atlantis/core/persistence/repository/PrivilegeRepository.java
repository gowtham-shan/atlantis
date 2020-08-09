package com.halnode.atlantis.core.persistence.repository;

import com.halnode.atlantis.core.persistence.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
}
