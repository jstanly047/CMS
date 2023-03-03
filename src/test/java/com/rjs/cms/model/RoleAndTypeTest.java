package com.rjs.cms.model;

import com.rjs.cms.model.common.FieldType;
import com.rjs.cms.model.common.HashType;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoleAndTypeTest {
    @Test
    public void checkRoleAndType(){
        try {
            RoleAndType roleAndType = RoleAndType.createRoleAndType(1L, FieldType.NORMAL.getValue(), HashType.MD5.getValue());
            assertEquals(roleAndType.getRole(), 1L);
            assertEquals(roleAndType.getType(), FieldType.NORMAL.getValue());
            assertEquals(roleAndType.getHashType(), HashType.MD5.getValue());
        }catch (Exception e)
        {
            fail("Unexpected exception!");
        }
    }

    @Test
    public void checkForExceptions(){
        assertThrows("Role Id is out of bound!",Exception.class, ()->RoleAndType.createRoleAndType(0x0FFFFFFFFFFFFFFFL,FieldType.HIDDEN.getValue(), HashType.NONE.getValue()));
        assertThrows("Role Id is out of bound!",Exception.class, ()->RoleAndType.createRoleAndType(0L,FieldType.HIDDEN.getValue(), HashType.NONE.getValue()));
        assertThrows("Type is invalid!",Exception.class, ()->RoleAndType.createRoleAndType(1L,-1, HashType.NONE.getValue()));
        assertThrows("Type is invalid!",Exception.class, ()->RoleAndType.createRoleAndType(1L,0x000000000000000FL, HashType.NONE.getValue()));
        assertThrows("Hash type is invalid!",Exception.class, ()->RoleAndType.createRoleAndType(1L,FieldType.HIDDEN.getValue(), -1));
        assertThrows("Hash type is invalid!",Exception.class, ()->RoleAndType.createRoleAndType(1L,FieldType.HIDDEN.getValue(), 0x000000000000002FL));
    }
}
