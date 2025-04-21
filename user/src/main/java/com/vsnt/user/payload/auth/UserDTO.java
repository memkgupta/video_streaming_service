package com.vsnt.user.payload.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private String userId;

    private String password;


    private String email;

    private String name;

    // Constructor
    public UserDTO(String userId, String password, String email, String name) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters


    // toString() for debugging
    @Override
    public String toString() {
        return "UserDTO{" +
                "userId='" + userId + '\'' +
                ", password='******'" +  // Hiding password for security
                ", email='" + email + '\'' +
                ", name=" + name +
                '}';
    }
}