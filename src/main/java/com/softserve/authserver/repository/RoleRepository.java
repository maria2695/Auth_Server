package com.softserve.authserver.repository;

import com.softserve.authserver.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}