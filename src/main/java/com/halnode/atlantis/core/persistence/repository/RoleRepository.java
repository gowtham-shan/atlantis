package com.halnode.atlantis.core.persistence.repository;

import com.halnode.atlantis.core.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
