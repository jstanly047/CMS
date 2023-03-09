package com.rjs.cms.model.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class TableInfo {
    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TableInfoCompositeKey implements Serializable {
        @Column(nullable = false)
        private String tableName;
        @Column(nullable = false)
        private String field;
    }

    @EmbeddedId
    private  TableInfoCompositeKey tableInfoCompositeKey;
    @Embedded
    private  RoleAndType roleAndType;
    private int numberOfChar=0;
    private int dataType;
    private int size=0;

    public TableInfo(String name, String fieldName, RoleAndType roleAndType, int numberOfChar, int dataType, int size){
        this.tableInfoCompositeKey = new TableInfoCompositeKey(name, fieldName);
        this.roleAndType = roleAndType;
        this.numberOfChar = numberOfChar;
        this.dataType = dataType;
        this.size = size;
    }
}
