package com.rjs.cms.service.db;

import com.rjs.cms.model.TableMeta;
import com.rjs.cms.model.UserInfoUpdate;
import com.rjs.cms.model.common.ConvertDataTypeToString;
import com.rjs.cms.model.common.TableMetaData;
import com.rjs.cms.repo.TableInfoRepo;
import org.h2.engine.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("h2db")
public class EntityMangerServiceTest {
    @Autowired
    ConvertDataTypeToString convertDataTypeToString;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TableInfoRepo tableInfoRepo;

    @Test
    public void testCreateTable() throws Exception {
        RoleInfoCache roleInfoCache = Mockito.mock(RoleInfoCache.class);
        Mockito.when(roleInfoCache.getRoleValue("Admin")).thenReturn(1L);
        TableMetaDataCache tableMetaDataCache = new TableMetaDataCache(tableInfoRepo);
        EntityMangerService entityMangerService = new EntityMangerService(roleInfoCache, tableMetaDataCache);
        entityMangerService.setEntityManager(entityManager);
        tableMetaDataCache.populateCache();

        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("TestT");
        tableMeta.setProperties(new ArrayList<>(List.of("user_id","c_int","c_str", "c_float", "c_double")));
        tableMeta.setDataTypes(new ArrayList<>(List.of("STRING","INTEGER", "STRING", "FLOAT", "DOUBLE")));
        tableMeta.setSizes(new ArrayList<>(List.of(60,0,60,0,0)));
        tableMeta.setRoles(new ArrayList<>(List.of("Admin","Admin", "Admin", "Admin", "Admin")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("NORMAL","NORMAL", "NORMAL", "NORMAL", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE","NONE", "NONE", "NONE", "NONE")));
        tableMeta.setNumberOfChars(new ArrayList<>(List.of(0,0,0,0,0)));
        TableMetaData tableMetaData = entityMangerService.createTable(tableMeta);
        assertNotNull(tableMetaData);

        tableMetaDataCache.populateCache();
        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("TestT");
        userInfoUpdate.setUserID("test_user");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("c_int","c_str", "c_float", "c_double")));
        userInfoUpdate.setValues(new ArrayList<>(List.of(1, "ms", 1.2, 1.5)));
        entityMangerService.updateUserData(userInfoUpdate);

        Query query = entityManager.createNativeQuery("SELECT count(*) FROM information_schema.tables WHERE table_name = :tableName");
        query.setParameter("tableName", tableMetaData.getTableName());
        BigInteger count = (BigInteger) query.getSingleResult();
        Assert.assertEquals(1L, count.longValue());
    }
}
