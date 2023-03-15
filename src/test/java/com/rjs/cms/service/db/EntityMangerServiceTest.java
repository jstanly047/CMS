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
        userInfoUpdate.add("A", "ADMIN", "NORMAL");
        userInfoUpdate.add(Short.valueOf((short) 1), "ADMIN", "NORMAL");
        userInfoUpdate.add(Integer.valueOf(2), "ADMIN", "NORMAL");
        userInfoUpdate.add(Long.valueOf(3L), "ADMIN", "NORMAL");
        userInfoUpdate.add("+94777123456", "ADMIN", "NORMAL");
        userInfoUpdate.add(Float.valueOf(1.2f), "ADMIN", "NORMAL");
        userInfoUpdate.add(Double.valueOf(1.5), "ADMIN", "NORMAL");
        userInfoUpdate.add(Boolean.valueOf(false), "ADMIN", "NORMAL");
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
        Mockito.when(roleInfoCache.getRoleString(1L)).thenReturn("ADMIN");
        Mockito.when(roleInfoCache.userHasRole(1,1)).thenReturn(true);

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
        assertEquals("Table [entity_manger_service_test] already exist", returnValue.getNotification().getMessage());
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
        userInfoUpdate.add("A", "ADMIN", "NORMAL");
        userInfoUpdate.add(Short.valueOf((short) 1), "ADMIN_NOT_EXIST", "NORMAL");

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
        userInfoUpdate.add("A", "ADMIN", "HIDDEN");
        userInfoUpdate.add(Short.valueOf((short) 1), "ADMIN", "NORMAL");

        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.CAN_NOT_SET_HIDDEN, notification.getNotificationStatus());
        assertEquals("Cannot set HIDDEN column [c_char] for table [entity_manger_service_test] for user [test_read_data]", notification.getMessage());
    }

    @Test
    public void testReadData(){
        createEntity_Manger_Service_Test();
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

        userInfo.setUserID("testReadData1");
        readRow = entityMangerService.readUserData(userInfo, 1L);
        assertEquals(true, readRow.isSuccess());
        assertEquals(0, readRow.getValue().size());

    }

    @Test
    public void testReadHiddenData(){
        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Hidden_Field_Read");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("phone", "nic", "address")));
        tableMeta.setDataTypes(new ArrayList<>(List.of("STRING","STRING","STRING")));
        tableMeta.setSizes(new ArrayList<>(List.of(12,12,60)));
        tableMeta.setRoles(new ArrayList<>(List.of("ADMIN", "ADMIN", "ADMIN")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("HIDDEN","HIDDEN","NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("MD5", "MD5", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(3,0,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isSuccess());
        tableMetaDataCache.populateCache();

        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Hidden_Field_Read");
        userInfoUpdate.setUserID("user1");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "nic" , "address")));
        userInfoUpdate.add("0102030405", "ADMIN", "HIDDEN");
        userInfoUpdate.add("6070809000", "ADMIN", "HIDDEN");
        userInfoUpdate.add("A1 Block St 1", "ADMIN", "NORMAL");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());

        UserInfo userInfo = new UserInfo();
        userInfo.setDomain("Hidden_Field_Read");
        userInfo.setUserID("user1");
        userInfo.setProperties(new ArrayList<>(List.of("phone", "nic" ,"address")));
        ReturnValue<List<JSONObject>> readRow = entityMangerService.readUserData(userInfo, 1L);
        assertEquals("{\"address\":\"A1 Block St 1\",\"phone\":\"*********405\",\"nic\":\"************\"}", readRow.getValue().get(0).toString());
    }

    @Test
    public void testValidatedDataAgainstHash(){

    }


    @Test
    public void testReadProtectedData(){
        Mockito.when(roleInfoCache.getRoleValue("CUSTOMER_CARE")).thenReturn(2L);
        Mockito.when(roleInfoCache.getRoleValue("MARKETING")).thenReturn(3L);
        Mockito.when(roleInfoCache.getRoleValue("MD_AND_CC")).thenReturn(4L);
        Mockito.when(roleInfoCache.getRoleValue("ALL")).thenReturn(5L);
        Mockito.when(roleInfoCache.userHasRole(2,2)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(2,3)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(2,4)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(2,5)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(3,2)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(3,3)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(3,4)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(3,5)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(4,2)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(4,3)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(4,4)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(4,5)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(5,2)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(5,3)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(5,4)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(5,5)).thenReturn(true);
        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Protected_Field_Read");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "address", "nic", "name")));
        tableMeta.setDataTypes(new ArrayList<>(List.of("STRING", "STRING", "STRING", "STRING", "STRING")));
        tableMeta.setSizes(new ArrayList<>(List.of(10, 10, 120, 12, 60)));
        tableMeta.setRoles(new ArrayList<>(List.of("CUSTOMER_CARE", "MARKETING", "MD_AND_CC", "ALL", "ALL")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("PROTECTED","PROTECTED","PROTECTED", "HIDDEN", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE", "NONE", "NONE", "MD5", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(3,0,0,4,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isSuccess());
        tableMetaDataCache.populateCache();

        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Protected_Field_Read");
        userInfoUpdate.setUserID("user_1@mail");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "address", "nic", "name")));
        userInfoUpdate.add("0102030405", "CUSTOMER_CARE", "PROTECTED");
        userInfoUpdate.add("01/01/2000", "MARKETING", "PROTECTED");
        userInfoUpdate.add("A1 Block St 1", "MD_AND_CC", "PROTECTED");
        userInfoUpdate.add("6070809000", "ALL", "HIDDEN");
        userInfoUpdate.add("user_1", "ALL", "NORMAL");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());

        UserInfo userInfo = new UserInfo();
        userInfo.setDomain("Protected_Field_Read");
        userInfo.setUserID("user_1@mail");
        userInfo.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "address", "nic", "name")));
        ReturnValue<List<JSONObject>> readRow = entityMangerService.readUserData(userInfo, 2L);
        assertEquals("{\"address\":\"A1 Block St 1\",\"phone\":\"0102030405\",\"date_of_birth\":\"**********\",\"name\":\"user_1\",\"nic\":\"********9000\"}", readRow.getValue().get(0).toString());
        readRow = entityMangerService.readUserData(userInfo, 3L);
        assertEquals("{\"address\":\"A1 Block St 1\",\"phone\":\"*******405\",\"date_of_birth\":\"01/01/2000\",\"name\":\"user_1\",\"nic\":\"********9000\"}", readRow.getValue().get(0).toString());
        readRow = entityMangerService.readUserData(userInfo, 4L);
        assertEquals("{\"address\":\"A1 Block St 1\",\"phone\":\"*******405\",\"date_of_birth\":\"**********\",\"name\":\"user_1\",\"nic\":\"********9000\"}", readRow.getValue().get(0).toString());
        readRow = entityMangerService.readUserData(userInfo, 5L);
        assertEquals("{\"address\":\"********************\",\"phone\":\"*******405\",\"date_of_birth\":\"**********\",\"name\":\"user_1\",\"nic\":\"********9000\"}", readRow.getValue().get(0).toString());
        readRow = entityMangerService.readUserData(userInfo, 1L);
        assertEquals("{\"address\":\"********************\",\"phone\":\"*******405\",\"date_of_birth\":\"**********\",\"name\":\"user_1\",\"nic\":\"********9000\"}", readRow.getValue().get(0).toString());
    }

    @Test
    public void overrideDefaultRoleByUserEntry(){
        Mockito.when(roleInfoCache.getRoleValue("CUSTOMER_CARE")).thenReturn(2L);
        Mockito.when(roleInfoCache.getRoleValue("ALL")).thenReturn(3L);
        Mockito.when(roleInfoCache.getRoleString(2L)).thenReturn("CUSTOMER_CARE");
        Mockito.when(roleInfoCache.getRoleString(3L)).thenReturn("ALL");
        Mockito.when(roleInfoCache.userHasRole(1,2)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(1,3)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(2,1)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(2,2)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(2,3)).thenReturn(true);
        Mockito.when(roleInfoCache.userHasRole(3,1)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(3,2)).thenReturn(false);
        Mockito.when(roleInfoCache.userHasRole(3,3)).thenReturn(true);


        TableMeta tableMeta = new TableMeta();
        tableMeta.setName("Override_Default_Role");
        tableMeta.setSizeOfUserId(60);
        tableMeta.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "nic", "name")));
        tableMeta.setDataTypes(new ArrayList<>(List.of("STRING", "STRING", "STRING", "STRING")));
        tableMeta.setSizes(new ArrayList<>(List.of(10, 10, 12, 60)));
        tableMeta.setRoles(new ArrayList<>(List.of("CUSTOMER_CARE",  "ALL", "ALL", "ALL")));
        tableMeta.setFieldTypes(new ArrayList<>(List.of("PROTECTED", "PROTECTED", "HIDDEN", "NORMAL")));
        tableMeta.setHashTypes(new ArrayList<>(List.of("NONE", "NONE", "MD5", "NONE")));
        tableMeta.setNumberOfVisibleChars(new ArrayList<>(List.of(3,0,4,0)));
        ReturnValue<TableMetaData> returnValue = entityMangerService.createTable(tableMeta);
        assertTrue(returnValue.isSuccess());
        tableMetaDataCache.populateCache();

        UserInfoUpdate userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Override_Default_Role");
        userInfoUpdate.setUserID("user_1@mail");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "nic", "name")));
        userInfoUpdate.add("0102030405", "ADMIN", "PROTECTED");
        userInfoUpdate.add("01/01/2000", "ADMIN", "PROTECTED");
        userInfoUpdate.add("6070809000", "ADMIN", "HIDDEN");
        userInfoUpdate.add("user_1", "ADMIN", "NORMAL");
        Notification notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.CAN_NOT_SET_ROLE, notification.getNotificationStatus());
        assertEquals("Can not set role [ADMIN] to column [phone] in table [override_default_role]. Role should have parent role [CUSTOMER_CARE]",
                        notification.getMessage());

        userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Override_Default_Role");
        userInfoUpdate.setUserID("user_2@mail");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "nic", "name")));
        userInfoUpdate.add("0102030405", "ALL", "PROTECTED");
        userInfoUpdate.add("01/01/2000", "ADMIN", "PROTECTED");
        userInfoUpdate.add("6070809000", "ADMIN", "HIDDEN");
        userInfoUpdate.add("user_2", "ADMIN", "NORMAL");
        notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.CAN_NOT_SET_ROLE, notification.getNotificationStatus());
        assertEquals("Can not set role [ALL] to column [phone] in table [override_default_role]. Role should have parent role [CUSTOMER_CARE]",
                notification.getMessage());

        userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Override_Default_Role");
        userInfoUpdate.setUserID("user_3@mail");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "nic", "name")));
        userInfoUpdate.add("0102030405", "CUSTOMER_CARE", "PROTECTED");
        userInfoUpdate.add("01/01/2000", "ADMIN", "PROTECTED");
        userInfoUpdate.add("6070809000", "ADMIN", "HIDDEN");
        userInfoUpdate.add("user_3", "ADMIN", "NORMAL");
        notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.CAN_NOT_SET_ROLE, notification.getNotificationStatus());
        assertEquals("Can not set role [ADMIN] to column [date_of_birth] in table [override_default_role]. Role should have parent role [ALL]",
                notification.getMessage());

        userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Override_Default_Role");
        userInfoUpdate.setUserID("user_4@mail");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "nic", "name")));
        userInfoUpdate.add("0102030405", "CUSTOMER_CARE", "PROTECTED");
        userInfoUpdate.add("01/01/2000", "CUSTOMER_CARE", "PROTECTED");
        userInfoUpdate.add("6070809000", "ALL", "HIDDEN");
        userInfoUpdate.add("user_4", "ALL", "NORMAL");
        notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());

        // FOR HIDDEN AND NORMAL DONT CHECK
        userInfoUpdate = new UserInfoUpdate();
        userInfoUpdate.setDomain("Override_Default_Role");
        userInfoUpdate.setUserID("user_5@mail");
        userInfoUpdate.setProperties(new ArrayList<>(List.of("phone", "date_of_birth", "nic", "name")));
        userInfoUpdate.add("0102030405", "CUSTOMER_CARE", "PROTECTED");
        userInfoUpdate.add("01/01/2000", "ALL", "PROTECTED");
        userInfoUpdate.add("6070809000", "ADMIN", "HIDDEN");
        userInfoUpdate.add("user_5", "ADMIN", "NORMAL");
        notification = entityMangerService.updateUserData(userInfoUpdate);
        assertEquals(NotificationStatus.SUCCESS, notification.getNotificationStatus());
    }

    @Test
    public void readMultipleDataWithFilter(){

    }
}
