package com.example.chillisauce.spaces.repository;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoxRepository extends JpaRepository<Box, Long> {
    Optional<Box> findByIdAndSpaceCompanies(Long boxId, Companies companies);
}
