package com.rjs.cms.service.db;

import com.rjs.cms.model.enity.Role;
import com.rjs.cms.model.enity.RoleRelation;
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
        List<Role> roles = Arrays.asList(new Role("ADMIN", 1L), new Role("RETAIL_USER", 2L));
        roleRepo.saveAll(roles);
        roleInfoCache = new RoleInfoCache(roleRepo, roleRelationRepo);
        roleInfoCache.populateCache();
        assertEquals(1L,roleInfoCache.getRoleValue("ADMIN"));
        assertEquals(2L, roleInfoCache.getRoleValue("RETAIL_USER"));
        assertEquals(0L, roleInfoCache.getRoleValue("CUSTOMER_CARE"));
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
