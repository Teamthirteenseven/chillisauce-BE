package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.User;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepositorySupport {
    Optional<User> findByIdAndCompanies_CompanyName (Long id, String companyName);
    List<User> findAllByCompanies_CompanyName (String companyName);
    List<User> findAllByUsernameContainingAndCompanies(String name, String companyName);    //검색
}
