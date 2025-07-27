package org.example.baro.user.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {
    private Long id;
    private UserRole role;
    private String email;
    private String password;

    @Builder
    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void changeRole() {
        this.role = UserRole.ADMIN;
    }
}
