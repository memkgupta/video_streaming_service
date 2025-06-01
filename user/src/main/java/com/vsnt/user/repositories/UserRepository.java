package com.vsnt.user.repositories;

import com.vsnt.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String username);
    List<User> findByIdIn(List<String> ids);
}
