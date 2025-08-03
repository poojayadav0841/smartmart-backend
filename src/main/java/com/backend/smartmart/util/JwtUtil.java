package com.backend.smartmart.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "smartmart_e_commerce_2025_JWT_secret_key_very_secure_token!";
    private static final int TOKEN_VALIDITY = 3600 * 5;

    // Get the username (subject) from the JWT token
    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); 
    }

    // Generic method to get a claim from the token
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token); // Get all claims from the token
        return claimsResolver.apply(claims); // Apply the function on the claims
    }

    // Extract all claims from the JWT token
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
    	
    	String userName = getUserNameFromToken(token);
    	return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }
    
    private boolean isTokenExpired(String token) {
    	final Date expirationDate = getExpirationDateFromToken(token);
    	return expirationDate.before(new Date());
    }
    
    private Date getExpirationDateFromToken(String token) {
    	return getClaimFromToken(token, Claims::getExpiration);
    	
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Create a Key object from the secret key
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(key) 
                .compact();
    }

}
