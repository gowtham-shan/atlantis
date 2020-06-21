package com.halnode.atlantis.core.persistence.repository;

import com.halnode.atlantis.core.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);
}
