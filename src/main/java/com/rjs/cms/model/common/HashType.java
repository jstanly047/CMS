package com.rjs.cms.model.common;

public enum HashType {
    NONE(0L),
    MD5(1L),
    SHA_1(2L),
    SHA_224(3L),
    SHA_256(4L),
    SHA_384(5L),
    SHA_512(6L),
    SHA3_224(7L),
    SHA3_256(8L),
    SHA3_384(9L),
    SHA3_512(10L),
    ARGON2(11L),
    BCRYPT(12L),
    AES_128(13L),
    AES_192(14L),
    AES_256(15L),
    RSA(16L);

    private final long value;

    private HashType(long value){
        this.value = value;
    }

    public long getValue(){
        return value;
    }
}
