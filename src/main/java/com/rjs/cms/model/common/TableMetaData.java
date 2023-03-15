package com.rjs.cms.model.common;

import com.rjs.cms.model.restapi.UserInfo;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;

@Data
public class TableMetaData {
    private HashMap<String, ColumnMetaData> columnMetaDataMap = new HashMap<>();
    private final String tableName;

    public TableMetaData(String tableName){
        this.tableName = tableName;
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

    public ReturnValue<Pair<String, Integer>> getColumnsForSQL(final UserInfo userInfo){
        ReturnValue<Pair<String, Integer>> returnValue = new ReturnValue<>();

        if (!tableName.equals(userInfo.getDomain())) {
            returnValue.fail(Notification.getTableNotFound(userInfo.getDomain()));
            return returnValue;
        }

        StringBuilder columns = new StringBuilder();
        int countFields = 0;

        for (String property : userInfo.getProperties()){
            ColumnMetaData columnMetaData = columnMetaDataMap.get(property);

            if (columnMetaData == null){
                returnValue.fail(Notification.getColumnNotFound(userInfo.getDomain(), property));
                return returnValue;
            }

            columns.append(columnMetaData.getSQLColumn());
            columns.append(", ");
            int fields = columnMetaData.isHidden() && columnMetaData.getNumberOfChar() < 1 ? 1 : 2;
            countFields += fields;
        }

        columns.delete(columns.length()-2, columns.length());
        returnValue.setValue(Pair.of(columns.toString(), countFields));
        return returnValue;
    }

    public Notification validate(){
        for (Map.Entry<String, ColumnMetaData> entry : columnMetaDataMap.entrySet()){
            if (entry.getValue().isHidden() && entry.getValue().getDataType() != DataType.STRING){
                return Notification.getHiddenDataMustBeString(tableName, entry.getKey());
            }
        }

        return Notification.getSuccess();
    }

    public void put(ColumnMetaData columnMetaData) {
        columnMetaDataMap.put(columnMetaData.getName(), columnMetaData);
    }

    public ColumnMetaData getColumnMetaData(String name){
        return columnMetaDataMap.get(name);
    }
}
