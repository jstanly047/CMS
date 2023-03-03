package com.rjs.cms.model.common;

import javassist.compiler.NoFieldException;

import java.util.LinkedList;
import java.util.List;

public class ReturnValue<T> {
    private boolean isSuccess = true;
    private Notification notification;
    private T value;

    public boolean isSuccess(){
        return notification == null;
    }

    public boolean isFailure(){
        return notification != null;
    }

    public void fail(Notification notification){
        this.notification = notification;
    }

    public Notification getNotification(){
        return notification;
    }

    public void setValue(T value){
        this.value = value;
    }

    public T getValue(){
        return value;
    }
}
