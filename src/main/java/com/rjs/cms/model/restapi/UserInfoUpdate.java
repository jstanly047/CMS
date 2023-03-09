package com.rjs.cms.model.restapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoUpdate extends UserInfo {

    @Data
    @NoArgsConstructor
    public static class ColumnInfo {
        Object value;
        String role;
        String fieldType;
        //Long hashType;
        String showValue;

        public ColumnInfo(Object value, String role, String fieldType, String showValue) {
            this.value = value;
            this.role = role;
            this.fieldType = fieldType.toUpperCase();
            this.showValue = showValue;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType.toUpperCase();
        }

        public void setRole(String role){
            this.role = role.toUpperCase();
        }
    }

    private List<ColumnInfo> columnsValues = new ArrayList<>();

    public void add(Object value, String role, String fieldType, String showValue){
        columnsValues.add(new ColumnInfo(value, role, fieldType, showValue));
    }
}
