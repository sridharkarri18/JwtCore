package com.example.JwtCore.repositories;

import com.example.JwtCore.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {


    Users findByEmail(String username);
}
