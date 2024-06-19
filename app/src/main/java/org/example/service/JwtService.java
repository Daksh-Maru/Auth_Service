package org.example.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// IT IS TO GENERATE TOKENS.
@Service
public class JwtService {
    //  THIS MUST BE STORED IN KEY MANAGER OR ENVIRONMENT FILES.
    public static final String SECRET = "A3F256EFA9B3C7D8E2A6F4B1C8D9E7F3A1B2C3D4E5F6A7B8C9D8E7F2A1B3C5D6";

    // EXTRACTING SPECIFIC CLAIMS (USERNAME AND EXPIRATION)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // VALIDATING USER WITH THE HELP OF THE CLAIMS FETCHED.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String GenerateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()*1000*60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    // THE MAIN LOGIN FOR FETCHING CLAIMS --- (TEDIOUS)
    // <T> FOR DEFINING GENERIC T FOR RETURN TYPE --> STRING/DATE.
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // CONVERTS THE SECRET KEY FOR SUITABLE OPERATION FOR VERIFICATION OF TOKENS.
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
