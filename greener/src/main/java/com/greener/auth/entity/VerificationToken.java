package com.greener.auth.entity;

import com.greener.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private LocalDateTime expiryDate;

    public String getToken() { return token; }
    public User getUser() { return user; }

    public void setToken(String token) { this.token = token; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}