package com.halnode.atlantis.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final Map<String, String> ORGANIZATION_SCHEMA_MAP = new HashMap<>();
    public static final String DEFAULT_TENANT = "public";
    public static final String DEFAULT_USER = "atlantis";
    public static final String JWT_SECRET_KEY = "$$_JWT_SECRET_TOKEN_$$";
    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;
    public static final String REMEMBER_ME_SECRET_KEY = "$$_ATLANTIS_REMEMBER_ME_SECRET_KEY$$";
}
