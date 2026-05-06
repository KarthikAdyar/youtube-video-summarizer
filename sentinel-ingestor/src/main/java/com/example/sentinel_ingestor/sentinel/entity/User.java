package com.example.sentinel_ingestor.sentinel.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false)
    private String provider; // e.g. google

    @Column(name = "provider_id", nullable = false)
    private String providerId; // provider subject / id

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password_hash")
    private String passwordHash;
}
