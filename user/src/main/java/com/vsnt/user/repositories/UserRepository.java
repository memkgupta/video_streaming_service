package com.vsnt.user.repositories;

import com.vsnt.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String username);
}
