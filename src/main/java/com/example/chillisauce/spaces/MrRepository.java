package com.example.chillisauce.spaces;

import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MrRepository extends JpaRepository <Mr, Long> {
    Optional<Mr> findByIdAndSpaceCompanies(Long mrId, Companies companies);
}
