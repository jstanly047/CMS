package com.rjs.cms.model;

import com.rjs.cms.model.common.*;
import com.rjs.cms.service.db.RoleInfoCache;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Data
public class TableMeta {
    private String name;
    private ArrayList<String> properties;
    private ArrayList<String> dataTypes;
    private ArrayList<Integer> sizes;
    private ArrayList<String> roles;
    private ArrayList<String> fieldTypes;
    private ArrayList<String> hashTypes;
    private ArrayList<Integer> numberOfChars;

    public TableMetaData getTableMetaData(final RoleInfoCache roleInfoCache) throws InvalidRoleAndType {
        TableMetaData tableMetaData = new TableMetaData(name);
        for (int i = 0; i < properties.size(); i++) {
            RoleAndType roleAndType = RoleAndType.createRoleAndType(roleInfoCache.getRoleValue(roles.get(i)),
                    FieldType.valueOf(fieldTypes.get(i)).getValue(),
                    HashType.valueOf(hashTypes.get(i)).getValue());
            ColumnMetaData columnMetaData = new ColumnMetaData(properties.get(i),
                    DataType.valueOf(dataTypes.get(i)),
                    sizes.get(i).intValue(),
                    roleAndType,
                    numberOfChars.get(i).intValue());
            tableMetaData.put(columnMetaData);
        }

        return tableMetaData;
    }

    public List<TableInfo> getTableInfos(final RoleInfoCache roleInfoCache) throws InvalidRoleAndType{
        List<TableInfo> tableInfos = new LinkedList<>();

        for (int i = 0; i < properties.size(); i++) {
            RoleAndType roleAndType = RoleAndType.createRoleAndType(roleInfoCache.getRoleValue(roles.get(i)),
                    FieldType.valueOf(fieldTypes.get(i)).getValue(),
                    HashType.valueOf(hashTypes.get(i)).getValue());
            tableInfos.add(new TableInfo(name, properties.get(i), roleAndType,
                    numberOfChars.get(i), DataType.valueOf(dataTypes.get(i)).getValue(),sizes.get(i)));
        }

        return  tableInfos;
    }
}