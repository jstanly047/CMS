package com.rjs.cms.service.db;

import com.rjs.cms.model.Role;
import com.rjs.cms.model.RoleRelation;
import com.rjs.cms.repo.RoleRelationRepo;
import com.rjs.cms.repo.RoleRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleInfoCacheTest {
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    RoleRelationRepo roleRelationRepo;
    RoleInfoCache roleInfoCache;

    @Test
    public void TestCheckRoleValue(){
        List<Role> roles = Arrays.asList(new Role("Admin", 1L), new Role("RetailUser", 2L));
        roleRepo.saveAll(roles);
        roleInfoCache = new RoleInfoCache(roleRepo, roleRelationRepo);
        roleInfoCache.populateCache();
        assertEquals(1L,roleInfoCache.getRoleValue("Admin"));
        assertEquals(2L, roleInfoCache.getRoleValue("RetailUser"));
        assertEquals(0L, roleInfoCache.getRoleValue("CustomerCare"));
    }

    @Test
    public void TestUserHasRole(){
        List<Role> roles = Arrays.asList(new Role("Admin", 1L), new Role("RetailUser", 2L));
        List<RoleRelation> roleRelations = Arrays.asList(new RoleRelation(new RoleRelation.Unique("RetailUser", "Admin")));
        roleRepo.saveAll(roles);
        roleRelationRepo.saveAll(roleRelations);
        roleInfoCache = new RoleInfoCache(roleRepo, roleRelationRepo);
        roleInfoCache.populateCache();
        assertTrue(roleInfoCache.userHasRole(1, 2));
        assertFalse(roleInfoCache.userHasRole(2, 1));
        assertFalse(roleInfoCache.userHasRole(1, 3));
    }
}
