package com.banku.openbankingservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        try {
            byte[] keyBytes = secretKey.getBytes();
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error al generar la clave de firma", e);
            throw new RuntimeException("Error al generar la clave de firma", e);
        }
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("Error al extraer el username del token", e);
            return null;
        }
    }
    
    public static String extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("No authentication found in SecurityContext");
            return null;
        }
        
        // Intentar obtener el userId de los detalles de autenticación
        if (authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            String userId = (String) details.get("userId");
            if (userId != null) {
                log.info("Found userId in authentication details: {}", userId);
                return userId;
            }
        }
        
        // Si no se encuentra el userId en los detalles, usar el nombre de usuario como fallback
        log.info("Using principal name as fallback: {}", authentication.getName());
        return authentication.getName();
    }

    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // Intentar obtener el userId directamente del claim principal
            String userId = claims.get("userId", String.class);
            if (userId != null) {
                log.info("Found userId in claims: {}", userId);
                return userId;
            }
            
            // Si no está directamente en los claims, intentar buscarlo en extraClaims
            Object extraClaimsObj = claims.get("extraClaims");
            if (extraClaimsObj instanceof Map) {
                Map<String, Object> extraClaims = (Map<String, Object>) extraClaimsObj;
                if (extraClaims.containsKey("userId")) {
                    userId = (String) extraClaims.get("userId");
                    log.info("Found userId in extraClaims: {}", userId);
                    return userId;
                }
            }
            
            // Si no se encuentra, retornar el subject como último recurso
            log.warn("userId not found in token, using subject as fallback");
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error al extraer el userId del token", e);
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error al procesar el token JWT: {}", e.getMessage());
            throw e;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error al verificar expiración del token: {}", e.getMessage());
            return true;
        }
    }
} 