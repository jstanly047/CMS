package com.rjs.cms.model.common;

import lombok.Data;

@Data
public class Notification {
    private final NotificationStatus notificationStatus;
    private final String message;

    private Notification(final NotificationStatus status, final String message) {
        this.notificationStatus = status;
        this.message = message;
    }

    public static Notification getSuccess(){
        return new Notification(NotificationStatus.SUCCESS, "Operation Success");
    }

    public static Notification getTableNotFound(String table) {
        return new Notification(NotificationStatus.TABLE_NOT_FOUND, "Table [" + table + "] not found");
    }

    public static Notification getColumnNotFound(String table, String column) {
        return new Notification(NotificationStatus.COLUMN_NOT_FOUND, "Table [" + table + "] does not have column [" + column + "]");
    }

    public static Notification getDBException(String message){
        return new Notification(NotificationStatus.DB_EXCEPTION, message);
    }

    public  static Notification getRoleNotFound(String table, String column, String role){
        return new Notification(NotificationStatus.ROLE_NOT_FOUND, "Cannot find role [" + role + "] specified for table [" + table +"] column [" + column + "]");
    }

    public  static Notification getCannotSetHidden(String table, String column, String user){
        return new Notification(NotificationStatus.CAN_NOT_SET_HIDDEN, "Cannot set HIDDEN column [" + column + "] for table [" + table +"] for user [" + user + "]");
    }

    public static Notification getHiddenDataMustBeString(String table, String columnName){
        return new Notification(NotificationStatus.INVALID_COLUMN, "Hidden column [" + columnName +"] must be string in table [" + table + "]");
    }
    public static Notification getTableAlreadyExist(String table){
        return new Notification(NotificationStatus.TABLE_ALREADY_EXIST, "Table [" + table + "] already exist");
    }

    public static Notification getInvalidRoleAndType(String message){
        return new Notification(NotificationStatus.INVALID_ROLE_TYPE, message);
    }

    public static Notification getInvalidRoleAndType(){
        return new Notification(NotificationStatus.INVALID_ROLE_TYPE, "Invalid field type/hash type");
    }

    public static Notification getCanNotSetRole(String tableName, String columnName, String role, String expectedRole){
        return new Notification(NotificationStatus.CAN_NOT_SET_ROLE, "Can not set role [" + role + "] to column [" + columnName + "] in table [" + tableName + "]. Role should have parent role [" + expectedRole + "]");
    }
}
