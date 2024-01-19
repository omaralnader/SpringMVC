package com.mouri.test.utils;

public enum RoleEnum {
	
    HIRING_MANAGER(RoleEnum.ROLE_HIRING_MANAGER),
    HQ_OFFICER(RoleEnum.ROLE_HQ_OFFICER),
    SUPER_USER(RoleEnum.ROLE_SUPER_USER),
    SYSTEM_ADMIN(RoleEnum.ROLE_SYSTEM_ADMIN);

    public static final String ROLE_HIRING_MANAGER = "ROLE_HIRING_MANAGER";

    public static final String ROLE_HQ_OFFICER = "ROLE_HQ_OFFICER";

    public static final String ROLE_SUPER_USER = "ROLE_SUPER_USER";

    public static final String ROLE_SYSTEM_ADMIN = "ROLE_SYSTEM_ADMIN";

    private final String code;
    
    private RoleEnum(String code) {
        this.code = code;
    }
}
