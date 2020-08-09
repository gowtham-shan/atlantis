package com.halnode.atlantis.util;

public class Constants {
    public static final String DEFAULT_TENANT = "public";
    public static final String DEFAULT_USER = "atlantis";
    public static final String JWT_SECRET_KEY = "$$_JWT_SECRET_TOKEN_$$";
    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;
    public static final String REMEMBER_ME_SECRET_KEY = "$$_ATLANTIS_REMEMBER_ME_SECRET_KEY$$";
    public static final int REMEMBER_ME_TOKEN_VALIDITY = 86400;
    public static final String URL_CONFIGURATIONS_FILE_LOCATION = "classpath:auth/url_configuration.json";
}
