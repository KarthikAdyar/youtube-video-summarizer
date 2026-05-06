package com.example.sentinel_ingestor.sentinel.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(OAuth2FailureHandler.class);
    private final String frontendRedirect;

    public OAuth2FailureHandler(org.springframework.core.env.Environment env) {
        this.frontendRedirect = env.getProperty("app.frontend-redirect-uri", "http://localhost:3000/oauth2/redirect");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("OAuth2 authentication failed: {}", exception.getMessage(), exception);

        String message = exception.getMessage() != null ? exception.getMessage() : "authentication_failed";
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String redirect = frontendRedirect + "?error=" + encoded;

        getRedirectStrategy().sendRedirect(request, response, redirect);
    }
}
