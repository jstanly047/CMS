package com.rjs.cms.model.common;


import javassist.bytecode.ByteArray;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

public enum DataType {
    STRING(0, String.class),
    CHAR(1, Byte.class),
    SHORT(2, Short.class),
    INTEGER(3, Integer.class),
    LONG(4, Long.class),
    //BIGINT(5),
    FLOAT(6, Float.class),
    DOUBLE(7, Double.class),
    BOOLEAN(8, Boolean.class),
    DATE(9, LocalDate.class),
    TIME(10, ZonedDateTime.class),
    DATE_TIME(11, ZonedDateTime.class),
    BINARY(12, ByteArray.class),
    UNDEFINED(100, String.class);

    private final int value;
    private final Class<?> javaType;
    private DataType(int value, Class<?> javaType){
        this.value=value;
        this.javaType = javaType;
    }

    public int getValue(){
        return value;
    }

    public final Class<?> getJavaType() { return javaType; }

    public static DataType fromValue(int i) {
        switch (i) {
            case 0:
                return STRING;
            case 1:
                return CHAR;
            case 2:
                return SHORT;
            case 3:
                return INTEGER;
            case 4:
                return LONG;
            case 6:
                return FLOAT;
            case 7:
                return DOUBLE;
            case 8:
                return BOOLEAN;
            case 9:
                return DATE;
            case 10:
                return TIME;
            case 11:
                return DATE_TIME;
            case 5:
            default:
                return UNDEFINED;
        }
    }
}
