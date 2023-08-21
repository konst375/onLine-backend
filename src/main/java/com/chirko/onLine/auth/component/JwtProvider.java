package com.chirko.onLine.auth.component;

import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.access.token.secret}") String jwtAccessSecret,
            @Value("${jwt.refresh.token.secret}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Date accessExpiration = Date.from(now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("roles", user.getRoles())
                .claim("name", user.getName())
                .claim("surname", user.getSurname())
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Date refreshExpiration = Date.from(now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
            throw new OnLineException("Token expired", ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
            throw new OnLineException("Unsupported jwt", ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
            throw new OnLineException("Malformed jwt", ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
            throw new OnLineException("Invalid signature", ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Invalid token", e);
            throw new OnLineException("Invalid token", ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
