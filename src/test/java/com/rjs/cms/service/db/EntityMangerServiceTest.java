package com.rjs.cms.service.db;

import com.rjs.cms.model.common.*;
import com.rjs.cms.model.restapi.TableMeta;
import com.rjs.cms.model.restapi.UserInfo;
import com.rjs.cms.model.restapi.UserInfoUpdate;
import com.rjs.cms.repo.TableInfoRepo;
import org.json.JSONObject;
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
import java.util.TreeMap;
import java.util.function.Function;

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

    RoleInfoCache roleInfoCache;
    TableMetaDataCache tableMetaDataCache;
    EntityMangerService entityMangerService;

    static boolean tableCreated = false;

    private UserInfoUpdate createUserInfoUpdate(String tableName, String userId){
        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain(tableName);
        userInfoUpdate.setUserID(userId);
        userInfoUpdate.setProperties(new ArrayList<>(List.of("c_char", "c_short" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool")));
        userInfoUpdate.add("A", "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Short.valueOf((short) 1), "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Integer.valueOf(2), "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Long.valueOf(3L), "ADMIN", "NORMAL", "");
        userInfoUpdate.add("+94777123456", "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Float.valueOf(1.2f), "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Double.valueOf(1.5), "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Boolean.valueOf(false), "ADMIN", "NORMAL", "");
        return  userInfoUpdate;
    }

    void createEntity_Manger_Service_Test(){
        if (tableCreated){
            return;
        }

        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Entity_Manger_Service_Test");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("c_char", "c_short" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool")));
        tableMeta.setDataTypes(new ArrayList<>(List.of("CHAR", "SHORT", "INTEGER", "LONG", "STRING", "FLOAT", "DOUBLE", "BOOLEAN")));
        tableMeta.setSizes(new ArrayList<>(List.of(0,0,0,0,60,0,0,0)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN", "ADMIN", "ADMIN", "ADMIN","ADMIN","ADMIN", "ADMIN", "ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("NORMAL", "NORMAL", "NORMAL", "NORMAL","NORMAL", "NORMAL", "NORMAL", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE", "NONE", "NONE", "NONE","NONE", "NONE", "NONE", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(0,0,0,0,0,0,0,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isSuccess() && returnValue.getValue() != null);
        tableMetaDataCache.populateCache();
        tableCreated = true;
    }

    @Before
    public  void setUp() throws  Exception{
        roleInfoCache = Mockito.mock(RoleInfoCache.class);
        Mockito.when(roleInfoCache.getRoleValue("ADMIN")).thenReturn(1L);

        tableMetaDataCache = new TableMetaDataCache(tableInfoRepo);
        entityMangerService = new EntityMangerService(roleInfoCache, tableMetaDataCache);
        entityMangerService.setEntityManager(entityManager);
        tableMetaDataCache.populateCache();
    }

    @Test
    public void testCreateTable() {
        createEntity_Manger_Service_Test();
        UserInfoUpdate userInfoUpdate = createUserInfoUpdate("Entity_Manger_Service_Test", "testCreateTable");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM Entity_Manger_Service_Test");
        BigInteger count = (BigInteger) query.getSingleResult();
        Assert.assertEquals(1L, count.longValue());
    }

    @Test
    public void checkTableCreate_DuplicateTableName(){
        createEntity_Manger_Service_Test();
        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Entity_Manger_Service_Test");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("c_char", "c_short" )));
        tableMeta.setDataTypes(new ArrayList<>(List.of("CHAR", "SHORT")));
        tableMeta.setSizes(new ArrayList<>(List.of(0,0)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN", "ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("NORMAL", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(0,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isFailure());
        assertEquals(NotificationStatus.TABLE_ALREADY_EXIST,returnValue.getNotification().getNotificationStatus());
        assertEquals("Table[entity_manger_service_test] already exist", returnValue.getNotification().getMessage());
    }

    @Test
    public void checkTableCreate_InvalidRoleAndType(){
        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Entity_Manger_Service_Test2");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("c_char", "c_short" )));
        tableMeta.setDataTypes(new ArrayList<>(List.of("CHAR", "SHORT")));
        tableMeta.setSizes(new ArrayList<>(List.of(0,0)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN", "ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("NORMALS", "NORMALS")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(0,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isFailure());
        assertEquals(NotificationStatus.INVALID_ROLE_TYPE,returnValue.getNotification().getNotificationStatus());
        assertEquals("Invalid field type/hash type", returnValue.getNotification().getMessage());
    }

    @Test
    public void checkTableCreate_DBException(){
        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Entity_Manger_Service_Test2");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("c_char", "c_char" )));
        tableMeta.setDataTypes(new ArrayList<>(List.of("CHAR", "CHAR")));
        tableMeta.setSizes(new ArrayList<>(List.of(0,0)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN", "ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("NORMAL", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(0,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isFailure());
        assertEquals(NotificationStatus.DB_EXCEPTION,returnValue.getNotification().getNotificationStatus());
    }

    @Test
    public void testReadData_TableNotExist(){
        UserInfo userInfo = new UserInfo();
        userInfo.setDomain("Entity_Manger_Service_Test_Not_Exist");
        userInfo.setUserID("testReadData");
        userInfo.setProperties(new ArrayList<>(List.of("c_char", "c_short" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool")));
        ReturnValue<List<JSONObject>> readRow = entityMangerService.readUserData(userInfo, 1L);
        assertEquals(true, readRow.isFailure());
        assertEquals(NotificationStatus.TABLE_NOT_FOUND, readRow.getNotification().getNotificationStatus());
        assertEquals("Table [entity_manger_service_test_not_exist] not found", readRow.getNotification().getMessage());
    }

    @Test
    public void testReadData(){
        UserInfoUpdate userInfoUpdate = createUserInfoUpdate("Entity_Manger_Service_Test", "testReadData");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());
        UserInfo userInfo = new UserInfo();
        userInfo.setDomain("Entity_Manger_Service_Test");
        userInfo.setUserID("testReadData");
        userInfo.setProperties(new ArrayList<>(List.of("c_char", "c_short" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool")));
        ReturnValue<List<JSONObject>> readRow = entityMangerService.readUserData(userInfo, 1L);
        assertEquals(true, readRow.isSuccess());
        assertEquals(1, readRow.getValue().size());
        assertEquals("{\"c_long\":3,\"c_str\":\"+94777123456\",\"c_float\":\"1.2\",\"c_double\":1.5,\"c_short\":1,\"c_char\":\"A\",\"c_int\":2,\"c_bool\":false}",
                readRow.getValue().get(0).toString());
    }

    @Test
    public void testReadData_ColumnNotFound(){
        UserInfoUpdate userInfoUpdate = createUserInfoUpdate("Entity_Manger_Service_Test", "columnNotFound");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());
        UserInfo userInfo = new UserInfo();
        userInfo.setDomain("Entity_Manger_Service_Test");
        userInfo.setUserID("columnNotFound");
        userInfo.setProperties(new ArrayList<>(List.of("c_char", "c_short" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool", "c_unavailable_col")));
        ReturnValue<List<JSONObject>> readRow = entityMangerService.readUserData(userInfo, 1L);
        assertEquals(true, readRow.isFailure());
        assertEquals(NotificationStatus.COLUMN_NOT_FOUND, readRow.getNotification().getNotificationStatus());
        assertEquals("Table [entity_manger_service_test] does not have column [c_unavailable_col]", readRow.getNotification().getMessage());
    }

    @Test
    public void updateUserInfo_tableNotExist(){
        UserInfoUpdate userInfoUpdate = createUserInfoUpdate("Entity_Manger_Service_Test_Not_Exist", "testReadData");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.TABLE_NOT_FOUND, notification.getNotificationStatus());
        assertEquals("Table [entity_manger_service_test_not_exist] not found", notification.getMessage());
    }

    @Test
    public void updateUserInfo_columnNotFound(){
        UserInfoUpdate userInfoUpdate = createUserInfoUpdate("Entity_Manger_Service_Test", "testReadData");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("c_char", "c_unavailable_col" ,"c_int", "c_long","c_str", "c_float", "c_double", "c_bool")));
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.COLUMN_NOT_FOUND, notification.getNotificationStatus());
        assertEquals("Table [entity_manger_service_test] does not have column [c_unavailable_col]", notification.getMessage());
    }

    @Test
    public void updateUserInfo_roleNotFound(){
        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Entity_Manger_Service_Test");
        userInfoUpdate.setUserID("testReadData");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("c_char", "c_short" )));
        userInfoUpdate.add("A", "ADMIN", "NORMAL", "");
        userInfoUpdate.add(Short.valueOf((short) 1), "ADMIN_NOT_EXIST", "NORMAL", "");

        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.ROLE_NOT_FOUND, notification.getNotificationStatus());
        assertEquals("Cannot find role [ADMIN_NOT_EXIST] specified for table [entity_manger_service_test] column [c_short]", notification.getMessage());
    }

    @Test
    public void tableCreate_setHiddeForStringOnly(){
        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Hidden_Only_For_String");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("not_str")));
        tableMeta.setSizes(new ArrayList<>(List.of(0)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("HIDDEN")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("MD5")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(0)));

        Function<String, Integer> test= (String type)->{
            tableMeta.setDataTypes(new ArrayList<>(List.of(type)));
            ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
            assertTrue(returnValue.isFailure());
            assertEquals(NotificationStatus.INVALID_COLUMN,returnValue.getNotification().getNotificationStatus());
            assertEquals("Hidden column [not_str] must be string in table [hidden_only_for_string]", returnValue.getNotification().getMessage());
            return 0;
        };

        for (String type : new ArrayList<>(List.of("CHAR", "SHORT", "INTEGER", "LONG", "FLOAT", "DOUBLE", "BOOLEAN", "DATE", "TIME", "DATE_TIME"))){
            test.apply(type);
        }

        tableMeta.setDataTypes(new ArrayList<>(List.of("STRING")));
        tableMeta.setSizes(new ArrayList<>(List.of(40)));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(5)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isSuccess());
    }

    @Test
    public void updateUserInfo_canNotSetHiddenForNonHidden(){
        createEntity_Manger_Service_Test();
        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Entity_Manger_Service_Test");
        userInfoUpdate.setUserID("test_Read_Data");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("c_char", "c_short" )));
        userInfoUpdate.add("A", "ADMIN", "HIDDEN", "");
        userInfoUpdate.add(Short.valueOf((short) 1), "ADMIN", "NORMAL", "");

        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.CAN_NOT_SET_HIDDEN, notification.getNotificationStatus());
        assertEquals("Cannot set HIDDEN column [c_char] for table [entity_manger_service_test] for user [test_read_data]", notification.getMessage());
    }
}
