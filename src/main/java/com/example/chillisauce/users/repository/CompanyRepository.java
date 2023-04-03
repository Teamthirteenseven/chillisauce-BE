package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Companies, Long> {
    Optional<Companies> findByCompanyName (String companyName);
    Optional<Companies> findByCertification (String certification);
}
