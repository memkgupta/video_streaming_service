package com.vsnt.user.repositories;

import com.vsnt.user.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,String> {
    Token findByToken(String token);
}
