package com.vsnt.user.controllers;

import com.vsnt.user.entities.User;
import com.vsnt.user.exceptions.APIException;
import com.vsnt.user.exceptions.BadRequestException;
import com.vsnt.user.exceptions.UserNotFoundException;
import com.vsnt.user.payload.ResetPasswordDTO;

import com.vsnt.user.payload.SimpleAPIResponse;
import com.vsnt.user.payload.auth.UserDTO;
import com.vsnt.user.services.CustomUserDetailsService;
import com.vsnt.user.services.JWTService;
import com.vsnt.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, JWTService jwtService, CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam("ids") List<String> ids) {
        List<User> users = userService.getAllUser(ids);
        return ResponseEntity.ok(users.stream().map(User::userDTO).toList());
    }

    @GetMapping("/{id}")
    public UserDTO getUserDetails(@PathVariable String id) throws UserNotFoundException {
        User user = userService.getUserDetails(id);
        if(user == null){
            throw new UserNotFoundException(id);
        }
        return user.userDTO();
    }

    @PatchMapping("/{id}")
    public UserDTO updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) throws UserNotFoundException {
        System.out.println(userDTO);
        User user = userService.updateUser(userDTO);
        if(user == null){
            throw new UserNotFoundException(id);
        }
        return user.userDTO();
    }
    public ResponseEntity<?> forgotPassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        User user = userService.getUserByEmail(resetPasswordDTO.getUserEmail());
        if(user == null){
            throw new UserNotFoundException(resetPasswordDTO.getUserEmail());
        }
        String token = jwtService.generateToken(user.getEmail());
        // TODO send this token to mail id
        return ResponseEntity.ok(SimpleAPIResponse.builder().message("Reset password link has been sent to the registered mail id").build());
    }
    @PutMapping("/reset-password")
    public ResponseEntity<?> updatePassword(@RequestBody ResetPasswordDTO updatePasswordDTO, HttpServletRequest request) throws APIException {

        String token = request.getParameter("token");
        if(token == null){
            throw new BadRequestException("Missing token");
        }
        String email = jwtService.extractUserName(token);
        User user = userService.getUserByEmail(email);
       if(user==null){
           throw new UserNotFoundException(email);
       }
        if(jwtService.isTokenExpired(token))
        {
            throw new BadRequestException("Expired token");
        }
        userService.updatePassword(user.getId(),updatePasswordDTO.getNewPass());
        return ResponseEntity.noContent().build();
    }

}
