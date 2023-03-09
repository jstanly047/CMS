package com.rjs.cms.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjs.cms.model.restapi.TableMeta;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableMetaTest {
    @Test
    public void checkJSONToUserInfo() throws  Exception{
        String json = "{\"name\":\"customersdetails\"," +
                "\"properties\":[\"name\",\"address\",\"age\"]," +
                "\"dataTypes\":[\"STRING\",\"STRING\",\"INTEGER\"]," +
                "\"sizes\":[60,120,0]," +
                "\"roles\":[\"ADMIN\",\"ADMIN\",\"ADMIN\"]," +
                "\"fieldTypes\":[\"NORMAL\",\"PROTECTED\",\"NONE\"]," +
                "\"hashTypes\":[\"NONE\",\"NONE\",\"NONE\"]," +
                "\"numberOfVisibleChars\":[0,0,0]" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        TableMeta tableMeta = objectMapper.readValue(json, TableMeta.class);
        assertEquals("customersdetails", tableMeta.getName());
        MatcherAssert.assertThat(tableMeta.getProperties(), Matchers.equalTo(new ArrayList<>(List.of("name", "address", "age"))));
        MatcherAssert.assertThat(tableMeta.getDataTypes(), Matchers.equalTo(new ArrayList<>(List.of("STRING", "STRING", "INTEGER"))));
        MatcherAssert.assertThat(tableMeta.getSizes(), Matchers.equalTo(new ArrayList<>(List.of(60,120,0))));
        MatcherAssert.assertThat(tableMeta.getRoles(), Matchers.equalTo(new ArrayList<>(List.of("ADMIN", "ADMIN", "ADMIN"))));
        MatcherAssert.assertThat(tableMeta.getFieldTypes(), Matchers.equalTo(new ArrayList<>(List.of("NORMAL", "PROTECTED", "NONE"))));
        MatcherAssert.assertThat(tableMeta.getHashTypes(), Matchers.equalTo(new ArrayList<>(List.of("NONE", "NONE", "NONE"))));
        MatcherAssert.assertThat(tableMeta.getNumberOfVisibleChars(), Matchers.equalTo(new ArrayList<>(List.of(0,0,0))));
    }

    @Test
    public void checkJSONToUserInfoCheckUpperAndLowerCases() throws  Exception{
        String json = "{\"name\":\"CustomersDetails\"," +
                "\"properties\":[\"Name\",\"ADDRESS\",\"aGe\"]," +
                "\"dataTypes\":[\"String\",\"string\",\"inTegEr\"]," +
                "\"sizes\":[60,120,0]," +
                "\"roles\":[\"admin\",\"Admin\",\"aDmin\"]," +
                "\"fieldTypes\":[\"normal\",\"Protected\",\"NoNe\"]," +
                "\"hashTypes\":[\"none\",\"None\",\"NoNe\"]," +
                "\"numberOfVisibleChars\":[0,0,0]" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        TableMeta tableMeta = objectMapper.readValue(json, TableMeta.class);
        assertEquals("customersdetails", tableMeta.getName());
        MatcherAssert.assertThat(tableMeta.getProperties(), Matchers.equalTo(new ArrayList<>(List.of("name", "address", "age"))));
        MatcherAssert.assertThat(tableMeta.getDataTypes(), Matchers.equalTo(new ArrayList<>(List.of("STRING", "STRING", "INTEGER"))));
        MatcherAssert.assertThat(tableMeta.getSizes(), Matchers.equalTo(new ArrayList<>(List.of(60,120,0))));
        MatcherAssert.assertThat(tableMeta.getRoles(), Matchers.equalTo(new ArrayList<>(List.of("ADMIN", "ADMIN", "ADMIN"))));
        MatcherAssert.assertThat(tableMeta.getFieldTypes(), Matchers.equalTo(new ArrayList<>(List.of("NORMAL", "PROTECTED", "NONE"))));
        MatcherAssert.assertThat(tableMeta.getHashTypes(), Matchers.equalTo(new ArrayList<>(List.of("NONE", "NONE", "NONE"))));
        MatcherAssert.assertThat(tableMeta.getNumberOfVisibleChars(), Matchers.equalTo(new ArrayList<>(List.of(0,0,0))));
    }
}
