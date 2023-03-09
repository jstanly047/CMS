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
        return new Notification(NotificationStatus.ROLE_NOT_FOUND, "Cannot set hide [" + column + "] for table [" + table +"] for user [" + user + "]");
    }
}
