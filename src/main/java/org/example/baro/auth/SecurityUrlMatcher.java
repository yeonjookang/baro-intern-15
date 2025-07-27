package org.example.baro.auth;

import java.util.Arrays;

public class SecurityUrlMatcher {
    public static final String[] PUBLIC_URLS = {
            "/signin",
            "/signup",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    };

    public static final String[] ADMIN_URLS = {
            "/admin/**"
    };

    public static boolean isPublicUrl(String path) {
        return Arrays.stream(PUBLIC_URLS).anyMatch(path::startsWith);
    }
}
