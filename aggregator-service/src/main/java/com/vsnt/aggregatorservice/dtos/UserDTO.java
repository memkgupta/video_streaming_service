package com.vsnt.aggregatorservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDTO  {

    private String id;
    private String username;
    private String email;
    private String name;
    private String bio;
    private String avatar;
    private String channelId;

    private Timestamp createdAt;


}