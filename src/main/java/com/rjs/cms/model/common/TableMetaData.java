package com.rjs.cms.model.common;

import com.rjs.cms.model.UserInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TableMetaData {
    private HashMap<String, ColumnMetaData> columnMetaDataMap = new HashMap<>();
    private final String tableName;

    public TableMetaData(String tableName){
        this.tableName = tableName;
    }

    public String getSQLColumn(){
        if (columnMetaDataMap.isEmpty())
        {
            return "";
        }

        StringBuilder columns = new StringBuilder();
        for (Map.Entry<String, ColumnMetaData> entry: columnMetaDataMap.entrySet()){
            columns.append(entry.getValue().getSQLColumn());
            columns.append(", ");
        }

        columns.delete(columns.length()-2, columns.length());
        return columns.toString();
    }

    public String getCreateSQL(){
        if (columnMetaDataMap.isEmpty())
        {
            return "";
        }

        StringBuilder queryStr = new StringBuilder("CREATE TABLE " + tableName + " ( ");

        for (Map.Entry<String, ColumnMetaData> entry: columnMetaDataMap.entrySet()){
            queryStr.append(entry.getValue().getCreateSQLColumn());
            queryStr.append(", ");
        }

        queryStr.delete(queryStr.length()-2, queryStr.length());
        queryStr.append(" );");
        return queryStr.toString();
    }

    public ReturnValue<String> getColumnsForSQL(final UserInfo userInfo){
        ReturnValue<String> returnValue = new ReturnValue<>();

        if (!tableName.equals(userInfo.getDomain())) {
            returnValue.fail(Notification.getTableNotFoundNotification(userInfo.getDomain()));
            return returnValue;
        }

        StringBuilder columns = new StringBuilder();

        for (String property : userInfo.getProperties()){
            ColumnMetaData columnMetaData = columnMetaDataMap.get(property);

            if (columnMetaData == null){
                returnValue.fail(Notification.getColumnNotFoundNotification(userInfo.getDomain(), property));
                return returnValue;
            }

            columns.append(columnMetaData.getSQLColumn());
            columns.append(", ");
        }
        columns.delete(columns.length()-2, columns.length());
        returnValue.setValue(columns.toString());
        return returnValue;
    }

    public void put(ColumnMetaData columnMetaData) {
        columnMetaDataMap.put(columnMetaData.getName(), columnMetaData);
    }

    public ColumnMetaData getColumnMetaData(String name){
        return columnMetaDataMap.get(name);
    }
}
