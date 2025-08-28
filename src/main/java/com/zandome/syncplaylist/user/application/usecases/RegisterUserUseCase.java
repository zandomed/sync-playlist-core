package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.application.dtos.commands.RegisterUserCommand;
import com.zandome.syncplaylist.user.application.dtos.response.AuthenticationResponse;
import com.zandome.syncplaylist.user.domain.exceptions.UserAlreadyExistsException;
import com.zandome.syncplaylist.user.domain.model.entities.User;
import com.zandome.syncplaylist.shared.domain.interfaces.UseCase;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserRepository;
import com.zandome.syncplaylist.user.domain.ports.services.JwtService;
import com.zandome.syncplaylist.user.domain.ports.services.PasswordEncoderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase implements UseCase<RegisterUserCommand, AuthenticationResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse execute(RegisterUserCommand command) {
        log.info("Attempting to register user with email: {}", command.email());
        
        if (userRepository.existsByEmail(command.email())) {
            log.warn("Registration failed - user already exists with email: {}", command.email());
            throw new UserAlreadyExistsException(command.email());
        }

        try {
            String encodedPassword = passwordEncoder.encode(command.password());

            User user = User.builder()
                    .name(command.name())
                    .lastName(command.lastName())
                    .email(command.email())
                    .password(encodedPassword)
                    .build();

            User savedUser = userRepository.save(user);
            String token = jwtService.generateToken(savedUser.getEmail());

            log.info("User successfully registered with email: {}", command.email());
            return new AuthenticationResponse(savedUser, token);
        } catch (Exception e) {
            log.error("Error during user registration for email: {}", command.email(), e);
            throw new RuntimeException("Failed to register user", e);
        }
    }
}