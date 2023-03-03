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

    public static Notification getSuccessNotification(){
        return new Notification(NotificationStatus.SUCCESS, "Operation Success");
    }

    public static Notification getTableNotFoundNotification(String table) {
        return new Notification(NotificationStatus.TABLE_NOT_FOUND, "Table [" + table + "] not found");
    }

    public static Notification getColumnNotFoundNotification(String table, String column) {
        return new Notification(NotificationStatus.COLUMN_NOT_FOUND, "Table [" + table + "] does not have column [" + column + "]");
    }

    public static Notification getDBExceptionNotification(String message){
        return new Notification(NotificationStatus.DB_EXCEPTION, message);
    }
}
