package com.rjs.cms.configuration;

import com.rjs.cms.model.common.DataType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StringConverterConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "oracle")
    public StringConverter oracleStringConverter(){
        return new OracleStringConverter();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "mysql")
    public StringConverter mySqlStringConverter(){
        return new MySqlStringConverter();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "mssql")
    public StringConverter msStringConverter(){
        return new MSStringConverter();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "postgre")
    public StringConverter postgreSQLStringConverter(){
        return new PostgreSQLStringConverter();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "h2db")
    public StringConverter h2DBStringConverter(){
        return new H2DBStringConverter();
    }

    public interface StringConverter{
        String getString(DataType dataType);
    }

    private class OracleStringConverter implements StringConverter{
        @Override
        public String getString(DataType dataType){
            switch (dataType){
                case STRING:
                    return "VARCHAR2";
                case CHAR:
                    return "CHAR";
                case SHORT:
                    return "SMALLINT";
                case INTEGER:
                    return "INTEGER";
                case LONG:
                    return "NUMBER(19)";
                case FLOAT:
                    return "BINARY_FLOAT";
                case DOUBLE:
                    return "BINARY_DOUBLE";
                case BOOLEAN:
                    return "BOOLEAN";
                case DATE:
                    return "DATE";
                case TIME:
                case DATE_TIME:
                    return "TIMESTAMP";
                case BINARY:
                    return "BLOB";
                default:
                    return "VARCHAR2";
            }
        }
    }

    private class MySqlStringConverter implements StringConverter{
        @Override
        public String getString(DataType dataType){
            switch (dataType) {
                case STRING:
                    return "VARCHAR";
                case CHAR:
                    return "VARCHAR(1)";
                case SHORT:
                case INTEGER:
                    return "INT";
                case LONG:
                    return "BIGINT";
                case FLOAT:
                    return "FLOAT";
                case DOUBLE:
                    return "DOUBLE";
                case BOOLEAN:
                    return "BIT";
                case DATE:
                    return "DATE";
                case TIME:
                case DATE_TIME:
                    return "DATETIME";
                case BINARY:
                    return "BLOB";
                default:
                    return "VARCHAR";
            }
        }
    }

    private class MSStringConverter implements StringConverter{
        @Override
        public String getString(DataType dataType){
            switch (dataType) {
                case STRING:
                    return "NVARCHAR";
                case CHAR:
                    return "NVARCHAR(1)";
                case SHORT:
                case INTEGER:
                    return "INT";
                case LONG:
                    return "BIGINT";
                case FLOAT:
                case DOUBLE:
                    return "FLOAT";
                case BOOLEAN:
                    return "BIT";
                case DATE:
                    return "DATE";
                case TIME:
                    return "TIME";
                case DATE_TIME:
                    return "DATETIME2";
                case BINARY:
                    return "VARBINARY(MAX)";
                default:
                    return "NVARCHAR";
            }
        }
    }

    private class PostgreSQLStringConverter implements StringConverter{
        @Override
        public String getString(DataType dataType){
            switch (dataType) {
                case STRING:
                    return "VARCHAR";
                case CHAR:
                    return "VARCHAR(1)";
                case SHORT:
                case INTEGER:
                case LONG:
                    return "INTEGER";
                case FLOAT:
                case DOUBLE:
                    return "DOUBLE PRECISION";
                case BOOLEAN:
                    return "BOOLEAN";
                case DATE:
                case TIME:
                case DATE_TIME:
                    return "TIMESTAMP";
                case BINARY:
                    return "BYTEA";
                default:
                    return "VARCHAR";
            }
        }
    }

    private class H2DBStringConverter implements StringConverter{
        @Override
        public String getString(DataType dataType) {
            switch (dataType) {
                case STRING:
                    return "VARCHAR";
                case CHAR:
                    return "CHAR";
                case SHORT:
                    return "SMALLINT";
                case INTEGER:
                    return "INT";
                case LONG:
                    return "BIGINT";
                case FLOAT:
                    return "REAL";
                case DOUBLE:
                    return "DOUBLE";
                case BOOLEAN:
                    return "BOOLEAN";
                case DATE:
                    return "DATE";
                case TIME:
                    return "TIME";
                case DATE_TIME:
                    return "TIMESTAMP";
                case BINARY:
                    return "BINARY";
                default:
                    return "VARCHAR";
            }
        }
    }
}

