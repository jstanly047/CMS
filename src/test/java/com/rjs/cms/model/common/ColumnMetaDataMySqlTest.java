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
@SpringBootTest(properties="cms.db.type=mysql")
public class ColumnMetaDataMySqlTest {
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
                     "FN VARCHAR(60), FN_show VARCHAR(4)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.HIDDEN.getValue(), HashType.MD5.getValue()), 0, DataType.STRING.getValue(), 60),
                    "FN VARCHAR(60)");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.PROTECTED.getValue(), HashType.MD5.getValue()), 4, DataType.STRING.getValue(), 60),
                         "FN VARCHAR(60), FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.CHAR.getValue(), 60),
                         "FN VARCHAR(1), FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.SHORT.getValue(), 60),
                         "FN INT, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.INTEGER.getValue(), 60),
                         "FN INT, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.LONG.getValue(), 60),
                         "FN BIGINT, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.FLOAT.getValue(), 60),
                         "FN FLOAT, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.DOUBLE.getValue(), 60),
                         "FN DOUBLE, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.BOOLEAN.getValue(), 60),
                         "FN BIT, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.DATE.getValue(), 60),
                         "FN DATE, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.TIME.getValue(), 60),
                         "FN DATETIME, FN_meta BIGINT");
            check.accept(new TableInfo("T", "FN", RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.NONE.getValue()), 4, DataType.DATE_TIME.getValue(), 60),
                         "FN DATETIME, FN_meta BIGINT");
        }
        catch (Exception e)
        {
            fail("Failed wile creating TableInfo");
        }
    }
}
