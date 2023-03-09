package com.rjs.cms.service.db;

import com.rjs.cms.model.common.*;
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
            if (columnMetaData.getRoleAndType().getType() == FieldType.HIDDEN.getValue()){
                retValues.put(row[offsetIndex+1], columnMetaData);
                offsetIndex += 3;
                continue;
            }

            RoleAndType roleAndType = new RoleAndType(((Number) row[offsetIndex+1]).longValue());

            if (roleInfoCache.userHasRole(userRole, roleAndType.getRole())){
                retValues.put(row[offsetIndex+1], columnMetaData);
            }else if (columnMetaData.getNumberOfChar() > 0){
                int n = columnMetaData.getSize() - columnMetaData.getNumberOfChar();
                String result;

                if (n > 0) {
                    result = new String(new char[n]).replace("\0", String.valueOf('X'));
                    result += ((String) row[offsetIndex + 1]).substring(n);
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

    private TreeMap<String, Object> getQueryResult(final String queryString, final UserInfo userInfo, long userRole){
        TreeMap<String, Object> retValue = new TreeMap<>();
        Query query = entityManager.createNativeQuery(queryString);
        List<Object[]> resultList;

        try {
            resultList = query.getResultList();
        }
        catch (Exception e){
            retValue.put("notification", Notification.getDBException(e.getMessage()));
            return  retValue;
        }

        for (Object[] row : resultList) {
                JSONObject response = readRow(row, userInfo, userRole);
                retValue.put("response", response);
        }

        return retValue;
    }

    @Transactional
    public TableMetaData createTable(final TableMeta addTable) throws Exception {

        if (tableMetaDataCache.contains(addTable.getName())){
            return null;
        }

        TableMetaData tableMetaData = addTable.getTableMetaData(roleInfoCache);
        addTable.getTableInfos(roleInfoCache).forEach(tableInfo -> entityManager.persist(tableInfo));
        int result = entityManager.createNativeQuery(tableMetaData.getCreateSQL()).executeUpdate();
        return result == 0 ? tableMetaData : null;
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
                " (user_id, " + columnsForSQLRetVal.getValue().getFirst() + ") VALUES (?,?" + numberOfParameters + ")";
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

                if (columnValue.getFieldType().isEmpty() == false && columnMetaData.getRoleAndType().getType() == FieldType.NORMAL.getValue()) {
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

    public TreeMap<String, Object> readUserData(final UserInfo userInfo, final long userRole) {
        TableMetaData tableMetaData = tableMetaDataCache.getTableMeta(userInfo.getDomain());

        if (tableMetaData == null){
            TreeMap<String, Object> retVal = new TreeMap<>();
            retVal.put("notification", Notification.getTableNotFound(userInfo.getDomain()));
            return retVal;
        }

        ReturnValue<Pair<String, Integer>> columnsForSQLRetVal = tableMetaData.getColumnsForSQL(userInfo);

        if (!columnsForSQLRetVal.isSuccess()){
            TreeMap<String, Object> retVal = new TreeMap<>();
            retVal.put("notification", columnsForSQLRetVal.getNotification());
            return retVal;
        }

        // userInfo.getDomain() been checked with cache for table names. so there can't be sql injections.
        String queryString = "SELECT " + columnsForSQLRetVal.getValue().getFirst() + " FROM " + userInfo.getDomain() + " where user_id = '" + userInfo.getUserID() + "'";
        return getQueryResult(queryString, userInfo, userRole);
    }
}
