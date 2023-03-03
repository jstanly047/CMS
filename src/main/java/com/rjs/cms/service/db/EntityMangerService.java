package com.rjs.cms.service.db;

import com.rjs.cms.model.*;
import com.rjs.cms.model.common.*;
import com.rjs.cms.repo.TableInfoRepo;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
            retValue.put("notification", Notification.getDBExceptionNotification(e.getMessage()));
            return  retValue;
        }

        for (Object[] row : resultList) {
                JSONObject response = readRow(row, userInfo, userRole);
                retValue.put("response", response);
        }

        return retValue;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TableMetaData createTable(final TableMeta addTable) throws Exception {

        if (tableMetaDataCache.contains(addTable.getName())){
            return null;
        }

        TableMetaData tableMetaData = addTable.getTableMetaData(roleInfoCache);
        addTable.getTableInfos(roleInfoCache).forEach(tableInfo -> entityManager.persist(tableInfo));
        int result = entityManager.createNativeQuery(tableMetaData.getCreateSQL()).executeUpdate();
        return result == 0 ? tableMetaData : null;
    }

    public Notification updateUserData(final UserInfoUpdate userInfoUpdate){
        TableMetaData tableMetaData = tableMetaDataCache.getTableMeta(userInfoUpdate.getDomain());

        if (tableMetaData == null){
            return Notification.getTableNotFoundNotification(userInfoUpdate.getDomain());
        }

        ReturnValue<String> columnsForSQLRetVal = tableMetaData.getColumnsForSQL(userInfoUpdate);

        if (!columnsForSQLRetVal.isSuccess()){
            return columnsForSQLRetVal.getNotification();
        }

        String numberOfParameters = ",?".repeat(userInfoUpdate.getProperties().size() - 1);
        String insertSQL = "INSERT INTO " +  userInfoUpdate.getDomain() +
                " (user_id, " + columnsForSQLRetVal.getValue() + ") VALUES (?,?," + numberOfParameters + ")";
        Query query = entityManager.createNativeQuery(insertSQL);
        int i = 1;

        query.setParameter(i, userInfoUpdate.getUserID());
        for (Object value : userInfoUpdate.getValues()){
            query.setParameter(++i, value);
        }

        try {
            query.executeUpdate();
        }catch (Exception e){
            return Notification.getDBExceptionNotification(e.getMessage());
        }

        return Notification.getSuccessNotification();
    }

    public TreeMap<String, Object> readUserData(final UserInfo userInfo, final long userRole) {
        TableMetaData tableMetaData = tableMetaDataCache.getTableMeta(userInfo.getDomain());

        if (tableMetaData == null){
            TreeMap<String, Object> retVal = new TreeMap<>();
            retVal.put("notification", Notification.getTableNotFoundNotification(userInfo.getDomain()));
            return retVal;
        }

        ReturnValue<String> columnsForSQLRetVal = tableMetaData.getColumnsForSQL(userInfo);

        if (!columnsForSQLRetVal.isSuccess()){
            TreeMap<String, Object> retVal = new TreeMap<>();
            retVal.put("notification", columnsForSQLRetVal.getNotification());
            return retVal;
        }

        // userInfo.getDomain() been checked with cache for table names. so there can't be sql injections.
        String queryString = "SELECT " + columnsForSQLRetVal.getValue() + " FROM " + userInfo.getDomain() + " where user_id = '" + userInfo.getUserID() + "'";
        return getQueryResult(queryString, userInfo, userRole);
    }
}
