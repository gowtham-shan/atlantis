package com.halnode.atlantis.core.persistence.repository;

import com.halnode.atlantis.core.persistence.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
}
