package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.application.dtos.commands.LoginUserCommand;
import com.zandome.syncplaylist.user.application.dtos.response.AuthenticationResponse;
import com.zandome.syncplaylist.user.domain.exceptions.InvalidCredentialsException;
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
public class LoginUserUseCase implements UseCase<LoginUserCommand, AuthenticationResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse execute(LoginUserCommand command) {
        log.info("Login attempt for email: {}", command.email());
        
        try {
            User user = userRepository.findByEmail(command.email())
                    .orElseThrow(() -> {
                        log.warn("Login failed - user not found with email: {}", command.email());
                        return new InvalidCredentialsException();
                    });

            if (!passwordEncoder.matches(command.password(), user.getPassword())) {
                log.warn("Login failed - invalid password for email: {}", command.email());
                throw new InvalidCredentialsException();
            }

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