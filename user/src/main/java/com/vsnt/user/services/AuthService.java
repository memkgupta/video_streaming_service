package com.vsnt.user.services;

import com.vsnt.user.config.UserDetailsImpl;
import com.vsnt.user.entities.Token;
import com.vsnt.user.entities.User;
import com.vsnt.user.exceptions.UserAlreadyExistsException;
import com.vsnt.user.payload.Token.TokenResponse;
import com.vsnt.user.payload.auth.LoginResponse;
import com.vsnt.user.payload.auth.RegisterRequest;
import com.vsnt.user.payload.auth.RegisterResponse;
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

    public RegisterResponse register(RegisterRequest userDTO) {


            User userAlreadyExists = userRepository.findByEmail(userDTO.getEmail());
            if (userAlreadyExists != null) {
                throw new UserAlreadyExistsException(userDTO.getEmail());
            }
            User user = new User();
            user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
            user.setEmail(userDTO.getEmail());
            user.setUsername(userDTO.getUsername());
            userRepository.save(user);
            UserDTO resUserDTO = new UserDTO();
            resUserDTO.setEmail(userDTO.getEmail());
            resUserDTO.setUsername(userDTO.getUsername());
            resUserDTO.setId(user.getId())
            ;
            resUserDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            String accessToken = jwtService.generateToken(userDTO.getEmail());
            String refreshToken = tokenService.generateToken(user);
            RegisterResponse response = new RegisterResponse();
            TokenResponse tr = TokenResponse.builder()

                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            response.setTokens(
                    tr
            );
            response.setUser(resUserDTO);
            return response;
        }



    public LoginResponse login(String email, String password) {
        try{
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
                System.out.println("Wrong password");
                throw new BadCredentialsException("Bad credentials");
            }
            String accessToken = jwtService.generateToken(email);
            String refreshToken = tokenService.generateToken(user);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setUsername(user.getUsername());
            userDTO.setId(user.getId());
            userDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            LoginResponse response  =new LoginResponse();
            response.setUser(userDTO);
            response.setTokens(TokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                    .build());
            return response;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Internal server error");
        }
    }

    public UserDTO authenticate(String token)
    {
        try{
            String email = jwtService.extractUserName(token);
            User user = userRepository.findByEmail(email);
            if (user == null) {
            throw new RuntimeException("User not found");
            }
           if(jwtService.isTokenValid(token,new UserDetailsImpl(user)))
           {
               UserDTO userDTO = new UserDTO();
               userDTO.setEmail(user.getEmail());
               userDTO.setUsername(user.getUsername());
               userDTO.setId(user.getId());
               userDTO.setAvatar(user.getAvatar());
               userDTO.setBio(user.getBio());
               userDTO.setChannelId(user.getChannelId());
               userDTO.setName(user.getName());
                userDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
               return userDTO;
           }
else{
    throw new RuntimeException("Invalid token");
           }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}