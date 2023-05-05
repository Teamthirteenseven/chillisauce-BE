package com.example.chillisauce.users.entity;

public enum UserRoleEnum {
    USER(Authority.USER),  // 사원 R
    ADMIN(Authority.ADMIN),  // 관리자 C.R.U.D
    MANAGER(Authority.MANAGER), //중간관리자 C.R.U

    SUPERUSER(Authority.SUPERUSER);  // 개발자용

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
        public static final String SUPERUSER = "ROLE_SUPERUSER";
    }
}