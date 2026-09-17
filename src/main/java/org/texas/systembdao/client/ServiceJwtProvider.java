package org.texas.systembdao.client;

import org.springframework.stereotype.Component;
import org.texas.systembdao.security.JwtUtil;

import java.util.List;

@Component
public class ServiceJwtProvider {

    private static final String SERVICE_ID = "SYSTEM_B";
    private static final List<String> SERVICE_ROLES = List.of("dob");

    private final JwtUtil jwtUtil;

    public ServiceJwtProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Generates a fresh service JWT on each call.
     * In production you'd cache this and refresh before expiry.
     * For the prototype, generating per call is simplest and always valid.
     */
    public String generateServiceToken() {
        return jwtUtil.generateServiceToken(SERVICE_ID, SERVICE_ROLES);
    }
}