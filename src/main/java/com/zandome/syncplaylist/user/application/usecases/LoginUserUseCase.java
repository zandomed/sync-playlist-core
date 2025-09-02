package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.application.dtos.commands.LoginUserCommand;
import com.zandome.syncplaylist.user.application.dtos.response.AuthenticationResponse;
import com.zandome.syncplaylist.user.domain.exceptions.InvalidCredentialsException;
import com.zandome.syncplaylist.user.domain.model.entities.User;
import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.shared.domain.interfaces.UseCase;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserRepository;
import com.zandome.syncplaylist.user.domain.ports.services.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginUserUseCase implements UseCase<LoginUserCommand, AuthenticationResponse> {

    private final UserRepository userRepository;
    private final FindUserAuthenticationUseCase findUserAuthenticationUseCase;
    private final ValidatePasswordUseCase validatePasswordUseCase;
    private final UpdateLastUsedUseCase updateLastUsedUseCase;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse execute(LoginUserCommand command) {
        log.info("Login attempt for email: {}", command.email());

        try {
            UserAuthentication userAuth = findUserAuthenticationUseCase
                    .findByEmailAndProvider(command.email(), AuthProvider.EMAIL)
                    .orElseThrow(() -> {
                        log.warn("Login failed - user authentication not found with email: {}", command.email());
                        return new InvalidCredentialsException();
                    });

            if (!validatePasswordUseCase.execute(command.password(), userAuth.getPasswordHash())) {
                log.warn("Login failed - invalid password for email: {}", command.email());
                throw new InvalidCredentialsException();
            }

            User user = userRepository.findById(userAuth.getUserId())
                    .orElseThrow(() -> {
                        log.error("Login failed - user not found with ID: {}", userAuth.getUserId());
                        return new InvalidCredentialsException();
                    });

            updateLastUsedUseCase.execute(userAuth.getId());
            String token = jwtService.generateToken(user.getEmail());

            log.info("User successfully logged in with email: {}", command.email());
            return new AuthenticationResponse(user, token);
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login for email: {}", command.email(), e);
            throw new RuntimeException("Failed to authenticate user", e);
        }
    }
}