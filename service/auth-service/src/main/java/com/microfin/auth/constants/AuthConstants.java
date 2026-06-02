package com.microfin.auth.constants;

/**
 * Centralized application constants.
 */
public final class AuthConstants {

    private AuthConstants() {
        // utility class
    }

    // Roles
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Providers
    public static final String PROVIDER_LOCAL = "LOCAL";

    // JWT claim keys
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_USER_ID = "uid";
    public static final String CLAIM_TOKEN_TYPE = "type";

    // Token types
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    // Headers
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // API paths
    public static final String API_BASE = "/api/v1";
    public static final String AUTH_BASE = API_BASE + "/auth";
    public static final String ADMIN_BASE = API_BASE + "/admin";

    // Login audit status
    public static final String LOGIN_SUCCESS = "SUCCESS";
    public static final String LOGIN_FAILED = "FAILED";
}
