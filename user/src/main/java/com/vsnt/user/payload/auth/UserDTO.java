package com.vsnt.user.payload.auth;

import com.vsnt.user.entities.User;
import com.vsnt.user.interfaces.DTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class UserDTO implements DTO<User> {

    private String id;
    private String username;
    private String email;
    private String name;
    private String bio;
    private String avatar;
    private String channelId;

    private Timestamp createdAt;

    @Override
    public User getObject() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setBio(bio);
        user.setAvatar(avatar);
        user.setChannelId(channelId);
        user.setCreatedAt(createdAt);

        return user;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", avatar='" + avatar + '\'' +
                ", channelId='" + channelId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}