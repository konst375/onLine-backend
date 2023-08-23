package com.chirko.onLine.auth.service;

import com.chirko.onLine.auth.JwtAuthentication;
import com.chirko.onLine.auth.component.JwtProvider;
import com.chirko.onLine.auth.dto.JwtAuthRequestDto;
import com.chirko.onLine.auth.dto.JwtAuthResponseDto;
import com.chirko.onLine.entities.RefreshToken;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.redis.RefreshTokenRepo;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthResponseDto login(@NonNull JwtAuthRequestDto authRequest) {
        final User user = (User) userDetailsService.loadUserByUsername(authRequest.getEmail());
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            saveRefreshToken(user, refreshToken);
            return new JwtAuthResponseDto(accessToken, refreshToken);
        } else {
            throw new OnLineException("Invalid password", ErrorCause.INVALID_PASSWORD, HttpStatus.FORBIDDEN);
        }
    }

    public JwtAuthResponseDto getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = getRefreshToken(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = (User) userDetailsService.loadUserByUsername(email);
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtAuthResponseDto(accessToken, null);
            }
        }
        return new JwtAuthResponseDto(null, null);
    }

    public JwtAuthResponseDto refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = getRefreshToken(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = (User) userDetailsService.loadUserByUsername(email);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                saveRefreshToken(user, newRefreshToken);
                return new JwtAuthResponseDto(accessToken, newRefreshToken);
            }
        }
        throw new OnLineException("Invalid JWT token", ErrorCause.INVALID_JWT_TOKEN, HttpStatus.FORBIDDEN);
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    private void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepo.save(RefreshToken.builder()
                .email(user.getEmail())
                .refreshToken(refreshToken)
                .build());
    }

    private String getRefreshToken(String email) {
        return refreshTokenRepo.findById(email)
                .orElseThrow(() -> new OnLineException(ErrorCause.REFRESH_TOKEN_NOT_FOUND, HttpStatus.FORBIDDEN))
                .getRefreshToken();
    }
}
