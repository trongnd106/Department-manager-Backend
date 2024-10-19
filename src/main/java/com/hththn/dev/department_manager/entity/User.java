package com.hththn.dev.department_manager.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;
    String email;
    String password;

    @Column(columnDefinition = "MEDIUMTEXT")
    String refreshToken;

    int isActive;

    @PrePersist
    protected void onCreate() {
        this.isActive = 1;
    }
}
