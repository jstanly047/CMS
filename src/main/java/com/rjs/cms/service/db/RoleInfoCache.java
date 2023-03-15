package com.rjs.cms.service.db;

import com.rjs.cms.model.enity.Role;
import com.rjs.cms.model.enity.RoleRelation;
import com.rjs.cms.repo.RoleRelationRepo;
import com.rjs.cms.repo.RoleRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Data
public class RoleInfoCache {
    class RoleNode {
        private String name;
        private long roleId;
        private boolean visited = false;
        private TreeSet<RoleNode> child = new TreeSet<>(new NodeIdComparator());
        private TreeSet<RoleNode> parent = new TreeSet<>(new NodeIdComparator());

        private RoleNode(Role role){
            this.name = role.getName();
            this.roleId = role.getId();
        }

        private void addParent(RoleNode parentNode){
            parent.add(parentNode);
        }

        private void addChild(RoleNode childNode){
            if (visited)
                return;

            child.add(childNode);
            visited = true;

            for (RoleNode grantParent : parent){
                grantParent.addChild(childNode);
            }

            visited = false;
        }

        private boolean hasRole(RoleNode roleNode){
            return child.contains(roleNode);
        }

    }

    class NodeIdComparator implements Comparator<RoleNode> {
        @Override public int compare(RoleNode n1, RoleNode n2) {
            if (n1.roleId > n2.roleId) {
                return -1;
            } else if (n1.roleId < n2.roleId) {
                return 1;
            }

            return 0;
        }
    }

    private final RoleRelationRepo roleRelationRepo;
    private final RoleRepo roleRepo;

    private HashMap<String, RoleNode> rolesCache = new HashMap<>();
    private HashMap<Long, String> roleIDToNameMap = new HashMap<>();

    private static AtomicLong localRelationCount = new AtomicLong();

    @Autowired
    public RoleInfoCache(RoleRepo roleRepo, RoleRelationRepo roleRelationRepo){
        this.roleRepo = roleRepo;
        this.roleRelationRepo = roleRelationRepo;
    }

    public void populateCache() {
        List<Role> roles = roleRepo.findAll();

        for (Role role: roles){
            rolesCache.put(role.getName(), new RoleNode(role));
            roleIDToNameMap.put(role.getId(),role.getName());
        }

        List<RoleRelation> roleRelations = roleRelationRepo.findAll();
        buildRoleRelation(roleRelations);
    }

    private void buildRoleRelation(List<RoleRelation> roleRelations) {
        long totalRelations = localRelationCount.get();
        for (RoleRelation roleRelation: roleRelations){
            RoleNode childNode = rolesCache.get(roleRelation.getUnique().getRole());

            if (childNode == null) {
                continue;
            }

            RoleNode parentNode = rolesCache.get(roleRelation.getUnique().getParentRole());

            if (parentNode == null) {
                continue;
            }

            parentNode.addChild(childNode);
            childNode.addParent(parentNode);
            totalRelations++;
        }

        localRelationCount.set(totalRelations);
    }

    public boolean userHasRole(long readingUserRole, long role){
        if (readingUserRole == role){
            return  true;
        }

        String readingUserRoleName = roleIDToNameMap.get(readingUserRole);

        if (readingUserRoleName == null){
            return  false;
        }

        String roleName = roleIDToNameMap.get(role);

        if (roleName == null){
            return  false;
        }

        return rolesCache.get(readingUserRoleName).hasRole(rolesCache.get(roleName));
    }

    public long getRoleValue(String role){
        RoleNode roleNode = rolesCache.get(role.toUpperCase());
        return roleNode != null ? roleNode.roleId : 0L;
    }

    public String getRoleString(long role){
        String roleName = roleIDToNameMap.get(role);
        return  roleName == null ? "" : roleName;
    }
}
