package com.example.chillisauce.users.entity;

public enum UserRoleEnum {
    USER(Authority.USER),  // 고객 권한
    ADMIN(Authority.ADMIN),  // 사업자 권한
    MANAGER(Authority.MANAGER),
    ;  // 사업자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String MANAGER = "ROLE_MANAGER";
    }
}