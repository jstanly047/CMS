package com.rjs.cms.model.common;

import com.rjs.cms.model.enity.RoleAndType;
import com.rjs.cms.model.enity.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColumnMetaData {
    private static String postFixMeta = "_meta";
    private static String postFixShow = "_show";
    private String name;
    private DataType dataType;
    private int size;
    private RoleAndType roleAndType;
    private int numberOfChar;

    public ColumnMetaData(final TableInfo tableInfo){
        name = tableInfo.getTableInfoCompositeKey().getField();
        roleAndType = tableInfo.getRoleAndType();
        numberOfChar = tableInfo.getNumberOfChar();
        dataType = DataType.fromValue(tableInfo.getDataType());
        size = tableInfo.getSize();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ColumnMetaData other = (ColumnMetaData) obj;
        return name.equals(other.name);
    }

    public String getCreateSQLColumn(){
        StringBuilder sql = new StringBuilder();
        sql.append(name);

        if (dataType == DataType.STRING){
            sql.append(" ");
            sql.append(ConvertDataTypeToString.getString(dataType));
            sql.append("(");
            sql.append(size);
            sql.append(")");

            if (isHidden()){
                sql.append(", ");
                sql.append(name);
                sql.append(postFixShow);
                sql.append(" ");
                sql.append(ConvertDataTypeToString.getString(dataType));
                sql.append("(");
                sql.append(numberOfChar);
                sql.append(")");
            }
        }
        else {
            sql.append(" ");
            sql.append(ConvertDataTypeToString.getString(dataType));
        }

        sql.append(", ");
        sql.append(name);
        sql.append(postFixMeta);
        sql.append(" ");
        sql.append(ConvertDataTypeToString.getString(DataType.LONG));
        return sql.toString();
    }

    public String getSQLColumn(){
        StringBuilder sql = new StringBuilder();
        sql.append(name);

        if (dataType == DataType.STRING){
            if (roleAndType.getType() == FieldType.HIDDEN.getValue()){
                sql.append(", ");
                sql.append(name);
                sql.append(postFixShow);
            }
        }

        sql.append(", ");
        sql.append(name);
        sql.append(postFixMeta);
        return sql.toString();
    }

    public boolean isHidden() {
        return roleAndType.getType() == FieldType.HIDDEN.getValue();
    }
    public boolean isProtected() {
        return roleAndType.getType() == FieldType.PROTECTED.getValue();
    }
}

