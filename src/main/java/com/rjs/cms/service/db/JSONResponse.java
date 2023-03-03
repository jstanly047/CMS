package com.rjs.cms.service.db;

import com.rjs.cms.model.common.ColumnMetaData;
import lombok.Data;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Date;

@Data
public class JSONResponse {
    private JSONObject response = new JSONObject();

    public void put(Object obj, final ColumnMetaData columnMetaData){
        switch (columnMetaData.getDataType()){
            case STRING:
                response.put(columnMetaData.getName(), (String) obj);
                break;
            case CHAR:
            case SHORT:
            case INTEGER:
            case LONG:
            case DOUBLE:
                response.put(columnMetaData.getName(), (Number) obj);
                break;
            case BOOLEAN:
                response.put(columnMetaData.getName(), (Boolean) obj);
                break;
            case DATE:
                response.put(columnMetaData.getName(), (Date) obj);
                break;
            case TIME:
            case DATE_TIME:
                response.put(columnMetaData.getName(), (Time) obj);
                break;
            default:
                response.put(columnMetaData.getName(), obj.toString());
        }
    }
}
