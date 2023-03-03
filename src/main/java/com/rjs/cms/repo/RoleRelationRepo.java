package com.rjs.cms.repo;

import com.rjs.cms.model.RoleRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRelationRepo extends JpaRepository<RoleRelation, RoleRelation.Unique> {
    List<RoleRelation> findAll();
    @Query("SELECT r FROM RoleRelation r WHERE r.unique.role IN :roles OR r.unique.parentRole IN :parentRoles")
    List<RoleRelation> findByRoleOrParentRole(@Param("roles") List<String> roles, @Param("parentRoles") List<String> parentRoles);
}
