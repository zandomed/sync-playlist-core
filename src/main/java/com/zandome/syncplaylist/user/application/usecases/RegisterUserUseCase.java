package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.application.dtos.commands.RegisterUserCommand;
import com.zandome.syncplaylist.user.application.dtos.response.AuthenticationResponse;
import com.zandome.syncplaylist.user.domain.model.entities.User;
import com.zandome.syncplaylist.shared.domain.interfaces.UseCase;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserRepository;
import com.zandome.syncplaylist.user.domain.ports.services.JwtService;
import com.zandome.syncplaylist.user.domain.ports.services.PasswordEncoderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase implements UseCase<RegisterUserCommand, AuthenticationResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse execute(RegisterUserCommand command) throws IllegalArgumentException {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("User with email already exists");
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = User.builder()
                .name(command.name())
                .lastName(command.lastName())
                .email(command.email())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthenticationResponse(savedUser, token);
    }
}