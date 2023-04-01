package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
