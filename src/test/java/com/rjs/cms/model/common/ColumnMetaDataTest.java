package com.rjs.cms.model.common;

import com.rjs.cms.model.enity.RoleAndType;
import com.rjs.cms.model.enity.TableInfo;
import org.junit.Test;

import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class ColumnMetaDataTest {
    @Test
    public void checkColumnMetaData(){
        TableInfo tableInfo = null;

        try {
            tableInfo = new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60);
        }
        catch (Exception e)
        {
            fail("Failed wile creating TableInfo");
        }

        ColumnMetaData columnMetaData = new ColumnMetaData(tableInfo);
        assertEquals(DataType.STRING, columnMetaData.getDataType());
        assertEquals("FN", columnMetaData.getName());
        assertEquals(4, columnMetaData.getNumberOfChar());
        assertEquals(60, columnMetaData.getSize());
        assertEquals(HashType.MD5.getValue(), columnMetaData.getRoleAndType().getHashType());
        assertEquals(1L, columnMetaData.getRoleAndType().getRole());
        assertEquals(FieldType.HIDDEN.getValue(), columnMetaData.getRoleAndType().getType());
    }

    @Test
    public void checkGetSQLColumn(){

        try {
            BiConsumer<TableInfo, String> check = (final TableInfo tableInfo1, final String expected)->{
                ColumnMetaData columnMetaData = new ColumnMetaData(tableInfo1);
                assertEquals(expected, columnMetaData.getSQLColumn());
            };

            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60),
                    "FN, FN_show");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue()), 0, DataType.STRING.getValue(), 60),
                    "FN");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.PROTECTED.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60),
                    "FN, FN_meta");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60),
                    "FN, FN_meta");
        }
        catch (Exception e)
        {
            fail("Failed wile creating TableInfo");
        }
    }
}
