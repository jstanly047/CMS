package com.rjs.cms.service.db;

import com.rjs.cms.model.common.*;
import com.rjs.cms.model.enity.InvalidRoleAndType;
import com.rjs.cms.model.enity.RoleAndType;
import com.rjs.cms.model.restapi.TableMeta;
import com.rjs.cms.model.restapi.UserInfo;
import com.rjs.cms.model.restapi.UserInfoUpdate;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component
@Data
public class EntityMangerService {
    @PersistenceContext
    private EntityManager entityManager;
    private final RoleInfoCache roleInfoCache;
    private final TableMetaDataCache tableMetaDataCache;

    @Autowired
    public EntityMangerService(RoleInfoCache roleInfoCache, TableMetaDataCache tableMetaDataCache){
        this.roleInfoCache = roleInfoCache;
        this.tableMetaDataCache = tableMetaDataCache;
    }

    private JSONObject readRow(final Object[] row, final UserInfo userInfo, final long userRole) {
        int offsetIndex = 0;
        JSONResponse retValues = new JSONResponse();
        TableMetaData tableMetaData = tableMetaDataCache.getTableMeta(userInfo.getDomain());

        for (final String property : userInfo.getProperties()){
            final ColumnMetaData columnMetaData = tableMetaData.getColumnMetaData(property);
            if (columnMetaData.isHidden()){
                retValues.put(row[offsetIndex+1], columnMetaData);
                offsetIndex += 3;
                continue;
            }

            RoleAndType roleAndType = new RoleAndType(((Number) row[offsetIndex+1]).longValue());

            if (columnMetaData.isProtected() == false || roleInfoCache.userHasRole(userRole, roleAndType.getRole())){
                retValues.put(row[offsetIndex], columnMetaData);
            }else if (columnMetaData.getNumberOfChar() > 0){
                int n = columnMetaData.getSize() - columnMetaData.getNumberOfChar();
                String result;

                if (n > 0) {
                    result = new String(new char[n]).replace("\0", String.valueOf('X'));
                    result += ((String) row[offsetIndex]).substring(n);
                }
                else{
                    result = new String(new char[columnMetaData.getSize()]).replace("\0", String.valueOf('X'));
                }

                retValues.put(result, columnMetaData);
            }

            offsetIndex += 2;
        }

        return retValues.getResponse();
    }

    private ReturnValue<List<JSONObject>> getQueryResult(final String queryString, final UserInfo userInfo, long userRole){
        ReturnValue<List<JSONObject>> retValue = new ReturnValue<>();
        Query query = entityManager.createNativeQuery(queryString);
        List<Object[]> resultList;

        try {
            resultList = query.getResultList();
        }
        catch (Exception e){
            retValue.fail(Notification.getDBException(e.getMessage()));
            return  retValue;
        }

        List<JSONObject> rows = new ArrayList<>();

        for (Object[] row : resultList) {
                rows.add(readRow(row, userInfo, userRole));
        }

        retValue.setValue(rows);
        return retValue;
    }

    @Transactional
    public ReturnValue<TableMetaData> createTable(final TableMeta addTable) {
        ReturnValue<TableMetaData> retVal = new ReturnValue<>();
        if (tableMetaDataCache.contains(addTable.getName())){
            retVal.fail(Notification.getTableAlreadyExist(addTable.getName()));
            return retVal;
        }

        try {
            TableMetaData tableMetaData = addTable.getTableMetaData(roleInfoCache);
            Notification tableMetaDataValidation = tableMetaData.validate();

            if (tableMetaDataValidation.getNotificationStatus() != NotificationStatus.SUCCESS){
                retVal.fail(tableMetaDataValidation);
                return retVal;
            }

            addTable.getTableInfos(roleInfoCache).forEach(tableInfo -> entityManager.persist(tableInfo));
            if(entityManager.createNativeQuery(tableMetaData.getCreateSQL()).executeUpdate() == 0) {
                retVal.setValue(tableMetaData);
            }
        }catch (InvalidRoleAndType invalidRoleAndType){
            retVal.fail(Notification.getInvalidRoleAndType(invalidRoleAndType.getMessage()));
        }
        catch (IllegalArgumentException illegalArgumentException){
            retVal.fail(Notification.getInvalidRoleAndType());
        }
        catch (Exception exception){
            retVal.fail(Notification.getDBException(exception.getMessage()));
        }
        return retVal;
    }

    @Transactional
    public Notification updateUserData(final UserInfoUpdate userInfoUpdate){
        TableMetaData tableMetaData = tableMetaDataCache.getTableMeta(userInfoUpdate.getDomain());

        if (tableMetaData == null){
            return Notification.getTableNotFound(userInfoUpdate.getDomain());
        }

        ReturnValue<Pair<String, Integer>> columnsForSQLRetVal = tableMetaData.getColumnsForSQL(userInfoUpdate);

        if (!columnsForSQLRetVal.isSuccess()){
            return columnsForSQLRetVal.getNotification();
        }

        String numberOfParameters = ",?".repeat( columnsForSQLRetVal.getValue().getSecond()- 1);
        String insertSQL = "INSERT INTO " +  userInfoUpdate.getDomain() +
                " (" + TableMeta.USER_FIELD_ID + ", " + columnsForSQLRetVal.getValue().getFirst() + ") VALUES (?,?" + numberOfParameters + ")";
        Query query = entityManager.createNativeQuery(insertSQL);
        int pIndex = 1;

        query.setParameter(pIndex++, userInfoUpdate.getUserID());

        try {
            int propertyIndex = 0;
            for (UserInfoUpdate.ColumnInfo columnValue : userInfoUpdate.getColumnsValues())
            {
                ColumnMetaData columnMetaData = tableMetaData.getColumnMetaData(userInfoUpdate.getProperties().get(propertyIndex));
                query.setParameter(pIndex++, columnValue.getValue());
                Long role = 0L;


                if (columnValue.getRole().isEmpty()) {
                    role = columnMetaData.getRoleAndType().getRole();
                }
                else{
                    role = roleInfoCache.getRoleValue(columnValue.getRole());
                }

                if (role == 0L)
                {
                    return Notification.getRoleNotFound(tableMetaData.getTableName(), columnMetaData.getName(), columnValue.getRole());
                }

                Long fieldType = columnMetaData.getRoleAndType().getType();

                if (columnValue.getFieldType().isEmpty() == false) {
                    fieldType = FieldType.valueOf(columnValue.getFieldType()).getValue();

                    if (fieldType == FieldType.HIDDEN.getValue() && columnMetaData.getRoleAndType().getType() != FieldType.HIDDEN.getValue()){
                        return Notification.getCannotSetHidden(tableMetaData.getTableName(), columnMetaData.getName(), userInfoUpdate.getUserID());
                    }
                }

                RoleAndType roleAndType = RoleAndType.createRoleAndType(role, fieldType, columnMetaData.getRoleAndType().getHashType());

                if (roleAndType.getType() == FieldType.HIDDEN.getValue()){
                    query.setParameter(pIndex++, columnValue.getShowValue());
                }

                query.setParameter(pIndex++, roleAndType.getRoleAndType());
                propertyIndex++;
            }

            query.executeUpdate();
        }catch (Exception e){
            return Notification.getDBException(e.getMessage());
        }

        return Notification.getSuccess();
    }

    public ReturnValue<List<JSONObject>> readUserData(final UserInfo userInfo, final long userRole) {
        TableMetaData tableMetaData = tableMetaDataCache.getTableMeta(userInfo.getDomain());

        if (tableMetaData == null){
            ReturnValue<List<JSONObject>> retVal = new ReturnValue<>();
            retVal.fail(Notification.getTableNotFound(userInfo.getDomain()));
            return retVal;
        }

        ReturnValue<Pair<String, Integer>> columnsForSQLRetVal = tableMetaData.getColumnsForSQL(userInfo);

        if (!columnsForSQLRetVal.isSuccess()){
            ReturnValue<List<JSONObject>> retVal = new ReturnValue<>();
            retVal.fail(columnsForSQLRetVal.getNotification());
            return retVal;
        }

        // userInfo.getDomain() been checked with cache for table names. so there can't be sql injections.
        String queryString = "SELECT " + columnsForSQLRetVal.getValue().getFirst() + " FROM " + userInfo.getDomain() + " where " + TableMeta.USER_FIELD_ID + " = '" + userInfo.getUserID() + "'";
        return getQueryResult(queryString, userInfo, userRole);
    }
}
