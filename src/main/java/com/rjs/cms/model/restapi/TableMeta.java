package com.rjs.cms.model.restapi;

import com.rjs.cms.model.enity.InvalidRoleAndType;
import com.rjs.cms.model.enity.RoleAndType;
import com.rjs.cms.model.enity.TableInfo;
import com.rjs.cms.model.common.*;
import com.rjs.cms.service.db.RoleInfoCache;
import lombok.Data;

import java.util.ArrayList;
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
    private ArrayList<Integer> numberOfVisibleChars;

    public void setName(String name){
        this.name = name.toLowerCase();
    }
    public void setProperties(List<String> properties){
        this.properties = new ArrayList<>();
        for (String property : properties){
            this.properties.add(property.toLowerCase());
        }
    }

    public void setDataTypes(List<String> dataTypes){
        this.dataTypes = new ArrayList<>();
        for (String dataType : dataTypes){
            this.dataTypes.add(dataType.toUpperCase());
        }
    }

    public void setRoles(List<String> roles){
        this.roles = new ArrayList<>();
        for (String role : roles){
            this.roles.add(role.toUpperCase());
        }
    }

    public void setFieldTypes(List<String> fieldTypes){
        this.fieldTypes = new ArrayList<>();
        for (String fieldType : fieldTypes){
            this.fieldTypes.add(fieldType.toUpperCase());
        }
    }

    public void setHashTypes(List<String> hashTypes){
        this.hashTypes = new ArrayList<>();
        for (String hashType : hashTypes){
            this.hashTypes.add(hashType.toUpperCase());
        }
    }

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
                    numberOfVisibleChars.get(i).intValue());
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
                    numberOfVisibleChars.get(i), DataType.valueOf(dataTypes.get(i)).getValue(),sizes.get(i)));
        }

        return  tableInfos;
    }
}