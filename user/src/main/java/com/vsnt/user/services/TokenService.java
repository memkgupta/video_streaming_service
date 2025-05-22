package com.vsnt.user.services;

import com.vsnt.user.entities.Token;
import com.vsnt.user.entities.User;
import com.vsnt.user.repositories.TokenRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    public TokenService(TokenRepository tokenRepository, JWTService jwtService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }
//    public boolean isTokenValid
    public String refreshToken(String token){
        System.out.println(token);
       Token t= tokenRepository.findByToken(token);
        System.out.println(t.getToken());
        System.out.println(t.getExpires()+" "+t.getExpires().before(new Date(Instant.now().toEpochMilli())));
       if(t==null){
           throw new RuntimeException("Token not found");
       }
       if(t.getExpires().before(new Date(Instant.now().toEpochMilli()))){
           throw new RuntimeException("Token is expired");
       }
       try {
           return jwtService.generateToken(t.getUser().getEmail());
       }
     catch (Exception e){
           e.printStackTrace();
           throw new RuntimeException("Some error occured");
     }
    }

    public String generateToken(User user) {
        String token = jwtService.generateToken(user.getEmail());
        Token t= new Token();
        t.setUser(user);
        t.setToken(token);
        t.setExpires(new Timestamp(System.currentTimeMillis()+1000*60*60*24));
        t.setCreated(new Timestamp(System.currentTimeMillis()));
        t.setUser(user);
        return tokenRepository.save(t).getToken();
    }
}
