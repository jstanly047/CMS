package com.rjs.cms.service.db;

import com.rjs.cms.model.RoleAndType;
import com.rjs.cms.model.TableInfo;
import com.rjs.cms.model.UserInfo;
import com.rjs.cms.model.common.DataType;
import com.rjs.cms.model.common.FieldType;
import com.rjs.cms.model.common.HashType;
import com.rjs.cms.repo.TableInfoRepo;
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
public class TableMetaDataCacheTest {
    @Autowired
    private TableInfoRepo tableInfoRepo;

    private TableMetaDataCache tableMetaDataCache;

    TableInfo getTableInfo(String tableName, String fieldName, long roleId, long fieldType, long hashType, int numberOfChar, int dataType, int size){
        TableInfo tableInfo = null;

        try {
            tableInfo = new TableInfo(tableName, fieldName, RoleAndType.createRoleAndType(roleId, fieldType, hashType), numberOfChar, dataType, size);
        }
        catch (Exception e)
        {
            fail("Failed wile creating TableInfo");
        }

        return tableInfo;
    }

    @Test
    public void checkContains(){
        List<TableInfo> tableInfos = Arrays.asList(getTableInfo("T1", "name", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.STRING.getValue(), 60 ),
                getTableInfo("T1", "phone", 1L, FieldType.PROTECTED.getValue(), HashType.NONE.ordinal(),  3, DataType.STRING.getValue(), 10 ), getTableInfo("T1", "address", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.STRING.getValue(), 120 ),
                getTableInfo("T2", "nid", 1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue(), 3, DataType.STRING.getValue(), 10 ),
                getTableInfo("T2", "age", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.INTEGER.getValue(), 0 ));
        tableInfoRepo.saveAll(tableInfos);
        tableMetaDataCache = new TableMetaDataCache(tableInfoRepo);
        tableMetaDataCache.populateCache();
        assertTrue(tableMetaDataCache.contains("T1"));
        assertTrue(tableMetaDataCache.contains("T2"));
        assertFalse(tableMetaDataCache.contains("T3"));
        //tableMetaDataCache.put("T3", null);
        //assertEquals(tableMetaDataCache.contains("T3"), true);
    }

    @Test
    public void checkGetColumnsForSQL(){
        List<TableInfo> tableInfos = Arrays.asList(getTableInfo("T1", "name", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.STRING.getValue(), 60 ),
                getTableInfo("T1", "phone", 1L, FieldType.PROTECTED.getValue(), HashType.NONE.ordinal(),  3, DataType.STRING.getValue(), 10 ),
                getTableInfo("T1", "address", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.STRING.getValue(), 120 ),
                getTableInfo("T1", "nid", 1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue(), 3, DataType.STRING.getValue(), 10 ),
                getTableInfo("T1", "age", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.INTEGER.getValue(), 0 ));

        tableInfoRepo.saveAll(tableInfos);
        tableMetaDataCache = new TableMetaDataCache(tableInfoRepo);
        tableMetaDataCache.populateCache();
        try {
            assertEquals(tableMetaDataCache.getTableMeta("T1").getColumnsForSQL(new UserInfo("T1", "Admin", Arrays.asList("name", "phone", "address", "nid", "age"))).getValue(),
                    "name, name_meta, phone, phone_meta, address, address_meta, nid, nid_show, nid_meta, age, age_meta");
            assertEquals(tableMetaDataCache.getTableMeta("T1").getColumnsForSQL(new UserInfo("T1", "Admin", Arrays.asList("name", "phone","age"))).getValue(),
                    "name, name_meta, phone, phone_meta, age, age_meta");
        }catch (Exception e)
        {
            fail("Not Expected");
        }
    }

    @Test
    public void checkGetColumnsForSQLThrowException(){
        List<TableInfo> tableInfos = Arrays.asList(getTableInfo("T1", "name", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.STRING.getValue(), 60 ),
                getTableInfo("T1", "phone", 1L, FieldType.PROTECTED.getValue(), HashType.NONE.ordinal(),  3, DataType.STRING.getValue(), 10 ),
                getTableInfo("T1", "address", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.STRING.getValue(), 120 ),
                getTableInfo("T1", "nid", 1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue(), 3, DataType.STRING.getValue(), 10 ),
                getTableInfo("T1", "age", 1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue(), 0, DataType.INTEGER.getValue(), 0 ));

        tableInfoRepo.saveAll(tableInfos);
        tableMetaDataCache = new TableMetaDataCache(tableInfoRepo);
        tableMetaDataCache.populateCache();
        assertNull(tableMetaDataCache.getTableMeta("T2"));
        assertEquals("Table [T1] does not have column [phone1]", tableMetaDataCache.getTableMeta("T1").getColumnsForSQL(new UserInfo("T1", "Admin", Arrays.asList("name", "phone1", "address", "nid", "age"))).getNotification().getMessage());
    }

}
