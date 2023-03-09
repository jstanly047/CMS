package com.rjs.cms.model.restapi;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String domain;
    private String userID;
    private List<String> properties;

    public void setDomain(String domain){
        this.domain = domain.toLowerCase();
    }

    public void setUserID(String userID){
        this.userID = userID.toLowerCase();
    }

    public void setProperties(List<String> properties){
        this.properties = new ArrayList<>();
        for (String property : properties){
            this.properties.add(property.toLowerCase());
        }
    }
}
