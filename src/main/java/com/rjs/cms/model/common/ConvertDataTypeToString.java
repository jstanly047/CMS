package com.rjs.cms.model.common;

import com.rjs.cms.configuration.StringConverterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class ConvertDataTypeToString {
    private static StringConverterConfig.StringConverter  stringConverter;

    @Autowired
    public ConvertDataTypeToString(StringConverterConfig.StringConverter stringConverter){
        ConvertDataTypeToString.stringConverter = stringConverter;
    }

    public static String getString(DataType dataType){
        return stringConverter.getString(dataType);
    }
}