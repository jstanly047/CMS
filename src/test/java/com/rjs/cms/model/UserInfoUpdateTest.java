package com.rjs.cms.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjs.cms.model.restapi.UserInfoUpdate;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class UserInfoUpdateTest {
    @Test
    public void checkJSONToUserInfo() throws  Exception{
        String json = "{\"domain\":\"customersdetails\"," +
                "\"userID\":\"username\"," +
                "\"properties\":[\"name\",\"address\",\"age\"]," +
                "\"columnsValues\":[{\"value\":\"John\",\"role\":\"*\",\"fieldType\":\"NORMAL\"}," +
                                    "{\"value\":\"Blk 1 East Street\",\"role\":\"ADD_CUSTOMER\",\"fieldType\":\"PROTECTED\"}," +
                                    "{\"value\":\"age\",\"role\":\"ADD_CUSTOMER\",\"fieldType\":\"HIDDEN\"}]" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoUpdate userInfoUpdate = objectMapper.readValue(json, UserInfoUpdate.class);
        assertEquals("username", userInfoUpdate.getUserID());
        assertEquals("customersdetails", userInfoUpdate.getDomain());
        MatcherAssert.assertThat(userInfoUpdate.getProperties(),
                Matchers.equalTo(new ArrayList<>(List.of("name", "address", "age"))));
        MatcherAssert.assertThat(userInfoUpdate.getColumnsValues(),
                Matchers.equalTo(new ArrayList<>(List.of(new UserInfoUpdate.ColumnInfo("John", "*", "NORMAL"),
                        new UserInfoUpdate.ColumnInfo("Blk 1 East Street", "ADD_CUSTOMER", "PROTECTED"),
                        new UserInfoUpdate.ColumnInfo("age", "ADD_CUSTOMER", "HIDDEN")))));
    }

    @Test
    public void checkJSONToUserInfoUpperCaseAndLowerCases() throws  Exception{
        String json = "{\"domain\":\"CustomersDetails\"," +
                "\"userID\":\"UserName\"," +
                "\"properties\":[\"NaMe\",\"address\",\"AGE\"]," +
                "\"columnsValues\":[{\"value\":\"John\",\"role\":\"*\",\"fieldType\":\"normal\"}," +
                "{\"value\":\"Blk 1 East Street\",\"role\":\"add_customer\",\"fieldType\":\"protected\"}," +
                "{\"value\":\"age\",\"role\":\"Add_Customer\",\"fieldType\":\"hiDDen\"}]" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoUpdate userInfoUpdate = objectMapper.readValue(json, UserInfoUpdate.class);
        assertEquals("username", userInfoUpdate.getUserID());
        assertEquals("customersdetails", userInfoUpdate.getDomain());
        MatcherAssert.assertThat(userInfoUpdate.getProperties(),
                Matchers.equalTo(new ArrayList<>(List.of("name", "address", "age"))));
        MatcherAssert.assertThat(userInfoUpdate.getColumnsValues(),
                Matchers.equalTo(new ArrayList<>(List.of(new UserInfoUpdate.ColumnInfo("John", "*", "NORMAL"),
                        new UserInfoUpdate.ColumnInfo("Blk 1 East Street", "ADD_CUSTOMER", "PROTECTED"),
                        new UserInfoUpdate.ColumnInfo("age", "ADD_CUSTOMER", "HIDDEN")))));
    }
}
