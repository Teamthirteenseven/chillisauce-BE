package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.entity.MultiBox;
import com.example.chillisauce.domain.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MultiBoxRepository extends JpaRepository<MultiBox, Long> {

    Optional<MultiBox> findByIdAndSpaceCompanies(Long multiBoxId, Companies companies);

}
