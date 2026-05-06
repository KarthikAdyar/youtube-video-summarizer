package com.example.sentinel_ingestor.sentinel.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public AuthController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/providers")
    public List<Object> providers() {
        List<Object> out = new ArrayList<>();
        if (clientRegistrationRepository == null) return out;

        // ClientRegistrationRepository may not be iterable; handle common implementations
        if (clientRegistrationRepository instanceof org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository) {
            org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository repo =
                    (org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository) clientRegistrationRepository;
            for (ClientRegistration r : repo) {
                out.add(new ProviderInfo(r.getRegistrationId(), r.getClientName()));
            }
            return out;
        }

        // Fallback: try common registration ids
        ClientRegistration r = clientRegistrationRepository.findByRegistrationId("google");
        if (r != null) out.add(new ProviderInfo(r.getRegistrationId(), r.getClientName()));
        return out;
    }

    record ProviderInfo(String id, String name){}
}
