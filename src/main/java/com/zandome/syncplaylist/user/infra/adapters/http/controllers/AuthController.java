package com.zandome.syncplaylist.user.infra.adapters.http.controllers;

import com.zandome.syncplaylist.user.application.dtos.commands.LoginUserCommand;
import com.zandome.syncplaylist.user.application.dtos.commands.RegisterUserCommand;
import com.zandome.syncplaylist.user.application.dtos.response.AuthenticationResponse;
import com.zandome.syncplaylist.user.application.usecases.FindUserAuthenticationUseCase;
import com.zandome.syncplaylist.user.application.usecases.LinkAuthMethodUseCase;
import com.zandome.syncplaylist.user.application.usecases.LoginUserUseCase;
import com.zandome.syncplaylist.user.application.usecases.RegisterUserUseCase;
import com.zandome.syncplaylist.user.application.usecases.UnlinkAuthMethodUseCase;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.request.LoginHttpRequest;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.request.RegisterHttpRequest;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.response.AuthMethodsHttpResponse;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.response.LoginHttpResponse;
import com.zandome.syncplaylist.user.infra.adapters.http.dtos.response.RegisterHttpResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final RegisterUserUseCase registerUserUseCase;
        private final LoginUserUseCase loginUserUseCase;
        private final FindUserAuthenticationUseCase findUserAuthenticationUseCase;
        private final LinkAuthMethodUseCase linkAuthMethodUseCase;
        private final UnlinkAuthMethodUseCase unlinkAuthMethodUseCase;

        @PostMapping("/register")
        public ResponseEntity<RegisterHttpResponse> register(@Valid @RequestBody RegisterHttpRequest request) {
                log.info("Registration request received for email: {}", request.email());

                RegisterUserCommand command = new RegisterUserCommand(
                                request.name(),
                                request.lastName(),
                                request.email(),
                                request.password());

                registerUserUseCase.execute(command);

                RegisterHttpResponse response = new RegisterHttpResponse(true);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginHttpResponse> login(@Valid @RequestBody LoginHttpRequest request) {
                log.info("Login request received for email: {}", request.email());

                LoginUserCommand command = new LoginUserCommand(
                                request.email(),
                                request.password());

                AuthenticationResponse result = loginUserUseCase.execute(command);

                LoginHttpResponse response = new LoginHttpResponse(result.token());
                return ResponseEntity.ok(response);
        }

        @GetMapping("/oauth2/{provider}")
        public ResponseEntity<Map<String, String>> initiateOAuth(@PathVariable String provider,
                        HttpServletRequest request) {
                log.info("OAuth2 login initiated for provider: {}", provider);

                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String authUrl = baseUrl + "/oauth2/authorization/" + provider.toLowerCase();

                return ResponseEntity.ok(Map.of(
                                "authUrl", authUrl,
                                "provider", provider));
        }

        @GetMapping("/success")
        public ResponseEntity<Map<String, String>> authSuccess(@RequestParam String token) {
                return ResponseEntity.ok(Map.of(
                                "token", token,
                                "status", "success"));
        }

        @GetMapping("/error")
        public ResponseEntity<Map<String, String>> authError() {
                return ResponseEntity.ok(Map.of(
                                "status", "error",
                                "message", "Authentication failed"));
        }

        @GetMapping("/methods/{userId}")
        public ResponseEntity<AuthMethodsHttpResponse> getUserAuthMethods(@PathVariable String userId) {
                var authMethods = findUserAuthenticationUseCase.getUserAuthMethods(userId);
                var providers = authMethods.stream()
                                .map(auth -> auth.getProvider().name().toLowerCase())
                                .toList();

                return ResponseEntity.ok(new AuthMethodsHttpResponse(providers));
        }

        @PostMapping("/link/{provider}")
        public ResponseEntity<Map<String, Boolean>> linkAuthMethod(
                        @PathVariable String provider,
                        @RequestParam String userId,
                        @RequestParam String email,
                        @RequestParam(required = false) String providerId,
                        @RequestParam(required = false) String password) {

                AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
                boolean success = linkAuthMethodUseCase.execute(userId, email, authProvider, providerId, password);

                return ResponseEntity.ok(Map.of("success", success));
        }

        @DeleteMapping("/unlink/{provider}")
        public ResponseEntity<Map<String, Boolean>> unlinkAuthMethod(
                        @PathVariable String provider,
                        @RequestParam String userId) {

                AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
                boolean success = unlinkAuthMethodUseCase.execute(userId, authProvider);

                return ResponseEntity.ok(Map.of("success", success));
        }
}