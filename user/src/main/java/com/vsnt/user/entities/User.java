package com.vsnt.user.entities;

import com.vsnt.user.payload.auth.UserDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
@Entity
@Data
@Table(name = "_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    private String avatar;
    private String bio;
    private String channelId;
    private String name;
    private Timestamp createdAt;
    private String password;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Token> tokens;

    public UserDTO userDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setAvatar(avatar);
        userDTO.setBio(bio);
        userDTO.setChannelId(channelId);
        return userDTO;
    }
}
