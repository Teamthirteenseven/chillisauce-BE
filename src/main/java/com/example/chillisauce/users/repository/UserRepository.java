package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositorySupport {
    Optional<User> findByEmail(String email);

    List<User> findAllByIdInAndCompanies_CompanyName(List<Long> userIds, String companyName);
}
