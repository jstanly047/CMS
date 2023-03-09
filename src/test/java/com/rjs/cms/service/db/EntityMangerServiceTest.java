package com.rjs.cms.service.db;

import com.rjs.cms.model.common.Notification;
import com.rjs.cms.model.common.NotificationStatus;
import com.rjs.cms.model.restapi.TableMeta;
import com.rjs.cms.model.restapi.UserInfoUpdate;
import com.rjs.cms.model.common.ConvertDataTypeToString;
import com.rjs.cms.model.common.TableMetaData;
import com.rjs.cms.repo.TableInfoRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

    private TableMeta tableMeta;
    private UserInfoUpdate userInfoUpdate;

    @Before
    public void setUp()
    {
        tableMeta = new TableMeta();
        tableMeta.setName("TestT");
        tableMeta.setProperties(new ArrayList<>(List.of("user_id", "c_char", "c_short" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool")));
        tableMeta.setDataTypes(new ArrayList<>(List.of("STRING", "CHAR", "SHORT", "INTEGER", "LONG", "STRING", "FLOAT", "DOUBLE", "BOOLEAN")));
        tableMeta.setSizes(new ArrayList<>(List.of(60,0,0,0,0,60,0,0,0)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN","ADMIN", "ADMIN", "ADMIN", "ADMIN","ADMIN","ADMIN", "ADMIN", "ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("NORMAL","NORMAL", "NORMAL", "NORMAL", "NORMAL","NORMAL", "NORMAL", "NORMAL", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE","NONE", "NONE", "NONE", "NONE","NONE", "NONE", "NONE", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(0,0,0,0,0,0,0,0,0)));

        userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("TestT");
        userInfoUpdate.setUserID("test_user");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("c_int","c_str", "c_float", "c_double")));
        userInfoUpdate.add(1, "ADMIN", "NORMAL", "");
        userInfoUpdate.add("ms", "ADMIN", "NORMAL", "");
        userInfoUpdate.add(1.2, "ADMIN", "NORMAL", "");
        userInfoUpdate.add(1.5, "ADMIN", "NORMAL", "");
    }

    @Test
    public void testCreateTable() throws Exception {
        RoleInfoCache roleInfoCache = Mockito.mock(RoleInfoCache.class);
        Mockito.when(roleInfoCache.getRoleValue("ADMIN")).thenReturn(1L);
        TableMetaDataCache tableMetaDataCache = new TableMetaDataCache(tableInfoRepo);
        EntityMangerService entityMangerService = new EntityMangerService(roleInfoCache, tableMetaDataCache);
        entityMangerService.setEntityManager(entityManager);
        tableMetaDataCache.populateCache();

        TableMetaData tableMetaData = entityMangerService.createTable(tableMeta);
        assertNotNull(tableMetaData);

        tableMetaDataCache.populateCache();
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());

        Query query = entityManager.createNativeQuery("SELECT count(*) FROM TestT");
        BigInteger count = (BigInteger) query.getSingleResult();
        Assert.assertEquals(1L, count.longValue());
    }
}
