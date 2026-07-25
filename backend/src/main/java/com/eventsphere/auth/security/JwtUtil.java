package com.eventsphere.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


/**
 * TODO: generateAccessToken(email, role), parseClaims(token), isValid(token)
 * using io.jsonwebtoken (jjwt). Key from jwt.secret in application.yml,
 * must be 32+ chars for HS256.
 */
@Component
public class JwtUtil {
	private final SecretKey key;
	private final long accessTokenExpiryMs;
	
	public JwtUtil(
			@Value("${jwt.secret}") String secret ,
			@Value("${jwt.access-token-expiry-ms}")long accessTokenExpiryMs) {
		this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpiryMs=accessTokenExpiryMs;	
	}
	public String getAccessToken(String email , String role){
		Date now = new Date();
		Date expiry = new Date(now.getTime()+accessTokenExpiryMs);
		return Jwts.builder()
				.subject(email)
				.claim("role",role)
				.issuedAt(now)
				.expiration(expiry)
				.signWith(key)
				.compact();
				
	}
	
	public Claims parseClaim(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public boolean isValid(String token) {
		try {
			parseClaim(token);
			return true;
		}
		catch(Exception e) {
			return false;
		}
		
	}
	
}
