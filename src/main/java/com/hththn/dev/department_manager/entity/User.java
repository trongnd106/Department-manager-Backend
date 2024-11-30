package com.hththn.dev.department_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;
    String email;
    String password;
    String authType;

    @Column(columnDefinition = "MEDIUMTEXT")
    String refreshToken;

    String googleAccountId;

    int isActive;

    @PrePersist
    protected void onCreate() {
        this.isActive = 1;
    }
}
