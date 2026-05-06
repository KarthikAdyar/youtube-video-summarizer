package com.example.sentinel_ingestor.sentinel.security;

import com.example.sentinel_ingestor.sentinel.entity.User;
import com.example.sentinel_ingestor.sentinel.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final String frontendRedirect;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil,
                                org.springframework.core.env.Environment env) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.frontendRedirect = env.getProperty("app.frontend-redirect-uri", "http://localhost:3000/oauth2/redirect");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = oauthUser.getAttributes();

        final String provider = (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken)
            ? ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()
            : "oauth";
        // Attempt to extract common attributes
        String providerId = attrs.getOrDefault("sub", attrs.getOrDefault("id", "")).toString();
        String email = attrs.getOrDefault("email", "").toString();
        String name = attrs.getOrDefault("name", "").toString();

        // Save or update local user
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setProvider(provider);
                    u.setProviderId(providerId);
                    return u;
                });

        user.setEmail(email);
        user.setName(name);
        userRepository.save(user);

        String subject = provider + ":" + providerId;
        String token = jwtUtil.generateToken(subject, user.getName(), user.getEmail());

        // Redirect to frontend with token as query param
        String redirectUrl = frontendRedirect + "?token=" + token;

        response.sendRedirect(redirectUrl);
    }
}
