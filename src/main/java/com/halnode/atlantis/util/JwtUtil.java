package com.halnode.atlantis.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    @NonNull
    private final DateUtil dateUtil;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(Constants.JWT_SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(dateUtil.getCurrentDateAsDate()).setExpiration(null)
                .signWith(SignatureAlgorithm.HS256, Constants.JWT_SECRET_KEY).compact();
    }

    public String createToken(String userIdentifier) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userIdentifier);
    }

    public boolean validateToken(String token, String userIdentifier) {
        final String userName = this.extractUserName(token);
        return (userName.equals(userIdentifier));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(dateUtil.getCurrentDateAsDate());
    }

}
