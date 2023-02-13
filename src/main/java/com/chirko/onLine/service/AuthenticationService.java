package com.chirko.onLine.service;

import com.chirko.onLine.dto.AuthenticationRequest;
import com.chirko.onLine.dto.RegisterRequest;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.entity.enums.Role;
import com.chirko.onLine.exceptions.UserAlreadyExitsException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import com.chirko.onLine.service.utils.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws UserAlreadyExitsException {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExitsException("User with this email already exist");
        }
        User user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthday(request.getBirthday())
                .role(Role.USER)
                .avatar(request.getAvatar())
                .build();
        userRepo.save(user);
        String jwtToken = jwtService.generateJwtToken(user);
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserEmailNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserEmailNotFoundException("No user with this email was found"));
        String jwtToken = jwtService.generateJwtToken(user);
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }
}
