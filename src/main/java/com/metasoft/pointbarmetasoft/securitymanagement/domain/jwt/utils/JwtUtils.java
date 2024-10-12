package com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils  {
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private String timeExpiration;

    //generate token access
    public String generateAccessToken(Authentication authentication, Object userAdditionalInformation ){//,Object userAdditionalInformation) {
        var authenticatedUser = (User) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> additionalClaimsMap = objectMapper.convertValue(userAdditionalInformation, Map.class);
        claims.putAll(additionalClaimsMap);

        return Jwts.builder()
                .claim( "role" ,authenticatedUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .addClaims(claims)
                .subject(authenticatedUser.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    //validar el token de acceso
    public boolean isTokenValid(String token){
        try {
            Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expirado: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("Error al validar el token: {}", e.getMessage());
            return false;
        }

    }

    //obtener el email del token
    public String getEmailFromToken(String token){
        return getClaims(token, Claims::getSubject);
    }

    //obtener un solo claims
    public <T> T getClaims(String token, Function<Claims,T> claimsFunction){
        Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    //obtener todos claims del token
    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //obtener firma del token
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    static public Set<String> getRoles(User authenticatedUser) {
        return authenticatedUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}