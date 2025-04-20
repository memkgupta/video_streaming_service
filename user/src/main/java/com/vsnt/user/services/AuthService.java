package com.vsnt.user.services;

import com.vsnt.user.config.UserDetailsImpl;
import com.vsnt.user.entities.Token;
import com.vsnt.user.entities.User;
import com.vsnt.user.payload.auth.LoginResponse;
import com.vsnt.user.payload.auth.UserDTO;
import com.vsnt.user.repositories.TokenRepository;
import com.vsnt.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder bCryptPasswordEncoder;
    private final JWTService jwtService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public String register(UserDTO userDTO) {

        try{
            User userAlreadyExists = userRepository.findByEmail(userDTO.getEmail());
            if (userAlreadyExists != null) {
                throw new RuntimeException("User already exists");
            }
            User user = new User();
            user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
            user.setEmail(userDTO.getEmail());
            user.setName(userDTO.getName());
            userRepository.save(user);


            return "User registered successfully";
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Internal server error");
        }
    }
    public LoginResponse login(String email, String password) {
        try{
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Bad credentials");
            }
            String accessToken = jwtService.generateToken(email);
            String refreshToken = tokenService.generateToken(user);
            return new LoginResponse(accessToken, refreshToken);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Internal server error");
        }
    }

    public boolean authenticate(String token)
    {
        try{
            String email = jwtService.extractUserName(token);
            User user = userRepository.findByEmail(email);
            if (user == null) {
            return false;
            }
            return jwtService.isTokenValid(token,new UserDetailsImpl(user));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}