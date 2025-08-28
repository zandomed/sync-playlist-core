package com.zandome.syncplaylist.user.infra.adapters.http.controllers;

import com.zandome.syncplaylist.user.application.dtos.commands.LoginUserCommand;
import com.zandome.syncplaylist.user.application.dtos.commands.RegisterUserCommand;
import com.zandome.syncplaylist.user.application.dtos.response.AuthenticationResponse;
import com.zandome.syncplaylist.user.application.usecases.LoginUserUseCase;
import com.zandome.syncplaylist.user.application.usecases.RegisterUserUseCase;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.request.LoginHttpRequest;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.request.RegisterHttpRequest;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.response.LoginHttpResponse;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.response.RegisterHttpResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final RegisterUserUseCase registerUserUseCase;
        private final LoginUserUseCase loginUserUseCase;

        @PostMapping("/register")
        public ResponseEntity<RegisterHttpResponse> register(@RequestBody RegisterHttpRequest request) {
                RegisterUserCommand command = new RegisterUserCommand(
                                request.name(),
                                request.lastName(),
                                request.email(),
                                request.password());

                try {
                        registerUserUseCase.execute(command);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(new RegisterHttpResponse(false));
                }

                RegisterHttpResponse response = new RegisterHttpResponse(
                                true);

                return ResponseEntity.ok(response);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginHttpResponse> login(@RequestBody LoginHttpRequest request) {
                LoginUserCommand command = new LoginUserCommand(
                                request.email(),
                                request.password());

                AuthenticationResponse result = loginUserUseCase.execute(command);

                LoginHttpResponse response = new LoginHttpResponse(
                                result.token());

                return ResponseEntity.ok(response);
        }
}