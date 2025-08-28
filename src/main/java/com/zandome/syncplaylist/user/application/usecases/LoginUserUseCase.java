package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.application.dtos.commands.LoginUserCommand;
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
public class LoginUserUseCase implements UseCase<LoginUserCommand, AuthenticationResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse execute(LoginUserCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthenticationResponse(user, token);
    }
}