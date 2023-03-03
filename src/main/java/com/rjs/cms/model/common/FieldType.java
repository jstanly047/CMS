package com.rjs.cms.model.common;

public enum FieldType {
    NORMAL(0L),
    HIDDEN(1L),
    PROTECTED(2L);

    private final long value;

    private FieldType(long value){
        this.value=value;
    }

    public long getValue(){
        return value;
    }
}
