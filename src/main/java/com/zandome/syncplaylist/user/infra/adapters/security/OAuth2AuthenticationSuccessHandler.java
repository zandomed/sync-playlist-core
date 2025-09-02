package com.zandome.syncplaylist.user.infra.adapters.security;

import com.zandome.syncplaylist.user.application.usecases.CreateOAuthAuthenticationUseCase;
import com.zandome.syncplaylist.user.application.usecases.FindUserAuthenticationUseCase;
import com.zandome.syncplaylist.user.application.usecases.UpdateLastUsedUseCase;
import com.zandome.syncplaylist.user.domain.model.entities.User;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserRepository;
import com.zandome.syncplaylist.user.domain.ports.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final FindUserAuthenticationUseCase findUserAuthenticationUseCase;
    private final CreateOAuthAuthenticationUseCase createOAuthAuthenticationUseCase;
    private final UpdateLastUsedUseCase updateLastUsedUseCase;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = getRegistrationId(request);

        try {
            User user = processOAuth2User(oAuth2User, registrationId);
            String token = jwtService.generateToken(user.getEmail());

            String redirectUrl = String.format("http://localhost:3000/auth/success?token=%s", token);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 authentication failed", e);
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/auth/error");
        }
    }

    private User processOAuth2User(OAuth2User oAuth2User, String registrationId) {
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        Map<String, Object> attributes = oAuth2User.getAttributes();

        UserInfo userInfo = extractUserInfo(attributes, provider);

        var existingAuth = findUserAuthenticationUseCase.findByProviderIdAndProvider(userInfo.getId(), provider);

        if (existingAuth.isPresent()) {
            updateLastUsedUseCase.execute(existingAuth.get().getId());
            return userRepository.findById(existingAuth.get().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        var existingUser = userRepository.findByEmail(userInfo.getEmail());
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            createOAuthAuthenticationUseCase.execute(user.getId(), userInfo.getEmail(), provider, userInfo.getId());
        } else {
            user = User.builder()
                    .name(userInfo.getFirstName())
                    .lastName(userInfo.getLastName())
                    .email(userInfo.getEmail())
                    .profilePictureUrl(userInfo.getPictureUrl())
                    .build();

            user = userRepository.save(user);
            createOAuthAuthenticationUseCase.execute(user.getId(), userInfo.getEmail(), provider, userInfo.getId());
        }

        return user;
    }

    private UserInfo extractUserInfo(Map<String, Object> attributes, AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> UserInfo.builder()
                    .id((String) attributes.get("sub"))
                    .email((String) attributes.get("email"))
                    .firstName((String) attributes.get("given_name"))
                    .lastName((String) attributes.get("family_name"))
                    .pictureUrl((String) attributes.get("picture"))
                    .build();

            case APPLE -> UserInfo.builder()
                    .id((String) attributes.get("sub"))
                    .email((String) attributes.get("email"))
                    .firstName(extractAppleName(attributes, "given_name"))
                    .lastName(extractAppleName(attributes, "family_name"))
                    .build();

            case SPOTIFY -> UserInfo.builder()
                    .id((String) attributes.get("id"))
                    .email((String) attributes.get("email"))
                    .firstName((String) attributes.get("display_name"))
                    .lastName("")
                    .pictureUrl(extractSpotifyImage(attributes))
                    .build();

            default -> throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        };
    }

    private String extractAppleName(Map<String, Object> attributes, String nameType) {
        Object nameObj = attributes.get("name");
        if (nameObj instanceof Map) {
            return (String) ((Map<?, ?>) nameObj).get(nameType);
        }
        return "";
    }

    private String extractSpotifyImage(Map<String, Object> attributes) {
        Object imagesObj = attributes.get("images");
        if (imagesObj instanceof java.util.List<?> images && !images.isEmpty()) {
            if (images.get(0) instanceof Map<?, ?> firstImage) {
                return (String) firstImage.get("url");
            }
        }
        return null;
    }

    private String getRegistrationId(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String[] pathSegments = requestUri.split("/");
        for (int i = 0; i < pathSegments.length - 1; i++) {
            if ("oauth2".equals(pathSegments[i]) && "code".equals(pathSegments[i + 1])) {
                return pathSegments[i + 2];
            }
        }
        throw new IllegalStateException("Could not extract registration ID from request URI");
    }

    private static class UserInfo {
        private final String id;
        private final String email;
        private final String firstName;
        private final String lastName;
        private final String pictureUrl;

        public UserInfo(String id, String email, String firstName, String lastName, String pictureUrl) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.pictureUrl = pictureUrl;
        }

        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPictureUrl() {
            return pictureUrl;
        }

        public static class UserInfoBuilder {
            private String id;
            private String email;
            private String firstName;
            private String lastName;
            private String pictureUrl;

            public UserInfoBuilder id(String id) {
                this.id = id;
                return this;
            }

            public UserInfoBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserInfoBuilder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public UserInfoBuilder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public UserInfoBuilder pictureUrl(String pictureUrl) {
                this.pictureUrl = pictureUrl;
                return this;
            }

            public UserInfo build() {
                return new UserInfo(id, email, firstName, lastName, pictureUrl);
            }
        }
    }
}