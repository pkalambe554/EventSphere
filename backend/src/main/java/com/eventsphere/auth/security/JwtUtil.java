package com.eventsphere.auth.security;

import org.springframework.stereotype.Component;

/**
 * TODO: generateAccessToken(email, role), parseClaims(token), isValid(token)
 * using io.jsonwebtoken (jjwt). Key from jwt.secret in application.yml,
 * must be 32+ chars for HS256.
 */
@Component
public class JwtUtil {
}
