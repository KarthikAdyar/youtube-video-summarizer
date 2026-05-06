package com.example.sentinel_ingestor.sentinel.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientRegistrationLogger implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(ClientRegistrationLogger.class);
    private final ClientRegistrationRepository repo;

    public ClientRegistrationLogger(ClientRegistrationRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (repo == null) {
            logger.info("No ClientRegistrationRepository bean available.");
            return;
        }

        List<String> ids = new ArrayList<>();

        if (repo instanceof InMemoryClientRegistrationRepository) {
            InMemoryClientRegistrationRepository in = (InMemoryClientRegistrationRepository) repo;
            for (ClientRegistration r : in) {
                ids.add(r.getRegistrationId());
            }
        } else {
            // Try common ids as a best-effort fallback
            String[] common = new String[]{"google", "github", "okta"};
            for (String id : common) {
                try {
                    ClientRegistration r = repo.findByRegistrationId(id);
                    if (r != null) ids.add(r.getRegistrationId());
                } catch (Exception ignored) {
                }
            }
        }

        if (ids.isEmpty()) {
            logger.info("No OAuth2 client registrations found.");
        } else {
            logger.info("Configured OAuth2 client registrations: {}", ids);
        }
    }
}
