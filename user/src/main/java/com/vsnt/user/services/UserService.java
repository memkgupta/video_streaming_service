package com.vsnt.user.services;

import com.vsnt.user.entities.User;
import com.vsnt.user.exceptions.UserNotFoundException;

import com.vsnt.user.payload.auth.UserDTO;
import com.vsnt.user.repositories.UserRepository;
import com.vsnt.user.utils.ObjectUpdater;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserDetails(String id)
    {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }
    public User getUserByEmail (String email)
    {
        return userRepository.findByEmail(email);
    }
    public User updateUser(UserDTO updateDTO)
    {
        User user = userRepository.findById(updateDTO.getId()).orElse(null);
        if(user == null)
        {
            return null;
        }
        user = ObjectUpdater.update(user, updateDTO);
        user = userRepository.save(user);
        return user;
    }
    public User updatePassword(String userId,String newPass) throws UserNotFoundException
    {
        User user = getUserDetails(userId);
        if(user == null){
            throw new UserNotFoundException(userId);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPass));
        user = userRepository.save(user);
        return user;
    }
    public List<User> getAllUser(List<String> ids)
    {
        return userRepository.findByIdIn(ids);
    }
}
