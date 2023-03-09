package com.rjs.cms.model.enity;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Access;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.persistence.AccessType;

@Embeddable
@Data
@NoArgsConstructor
@Access(AccessType.FIELD)
public class RoleAndType {
    private long roleAndType = 0;
    @Transient
    static final long roleMask = 0x00FFFFFFFFFFFFFFL;
    @Transient
    static final long typeMask = 0xE000000000000000L;
    @Transient
    static final long hashTypeMask = 0x1F00000000000000L;
    public static RoleAndType createRoleAndType(long roleId, long type, long hashType) throws InvalidRoleAndType{
        if (roleId < 1 || roleId > 0x00FFFFFFFFFFFFFFL){
            throw new InvalidRoleAndType("Role Id is out of bound!");
        }

        if (type < 0 || type > 0x000000000000000EL){
            throw new InvalidRoleAndType("Type is invalid!");
        }

        if (hashType < 0 || hashType > 0x000000000000001FL){
            throw new InvalidRoleAndType("Hash type is invalid!");
        }

        return new RoleAndType(roleId, type, hashType);
    }
    private RoleAndType(long roleId, long type, long hashType){
        type = type << 61;
        hashType = hashType << 56;
        this.roleAndType = roleId | type | hashType;
    }
    public RoleAndType(long roleAndType){
        this.roleAndType = roleAndType;
    }
    public long getRole(){
        return roleAndType & roleMask;
    }
    public long getType(){
        return (roleAndType & typeMask) >> 61;
    }
    public long getHashType(){
        return (roleAndType & hashTypeMask) >> 56;
    }
}
