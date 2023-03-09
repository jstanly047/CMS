package com.rjs.cms.model.common;

import com.rjs.cms.model.enity.RoleAndType;
import com.rjs.cms.model.enity.TableInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(properties="cms.db.type=oracle")
public class ColumnMetaDataOracleTest {
    @Autowired
    ConvertDataTypeToString convertDataTypeToString;

    @Test
    public void checkGetCreateSQLColumn(){
        try {
            BiConsumer<TableInfo, String> check = (final TableInfo tableInfo1, final String expected)->{
                ColumnMetaData columnMetaData = new ColumnMetaData(tableInfo1);
                assertEquals(expected, columnMetaData.getCreateSQLColumn());
            };

            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60),
                     "FN VARCHAR2(60), FN_show VARCHAR2(4), FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.PROTECTED.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60),
                         "FN VARCHAR2(60), FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.CHAR.getValue(), 60),
                         "FN CHAR, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.SHORT.getValue(), 60),
                         "FN SMALLINT, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.INTEGER.getValue(), 60),
                         "FN INTEGER, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.LONG.getValue(), 60),
                         "FN NUMBER(19), FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.FLOAT.getValue(), 60),
                         "FN BINARY_FLOAT, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.DOUBLE.getValue(), 60),
                         "FN BINARY_DOUBLE, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.BOOLEAN.getValue(), 60),
                         "FN BOOLEAN, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.DATE.getValue(), 60),
                         "FN DATE, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.TIME.getValue(), 60),
                         "FN TIMESTAMP, FN_meta NUMBER(19)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.DATE_TIME.getValue(), 60),
                         "FN TIMESTAMP, FN_meta NUMBER(19)");
        }
        catch (Exception e)
        {
            fail("Failed wile creating TableInfo");
        }
    }
}
