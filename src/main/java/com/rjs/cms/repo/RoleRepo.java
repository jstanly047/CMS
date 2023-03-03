package com.rjs.cms.repo;

import com.rjs.cms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {
    List<Role> findAll();
}
