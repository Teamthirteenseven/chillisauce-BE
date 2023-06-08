package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.entity.Space;
import com.example.chillisauce.domain.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long>, SpaceRepositorySupport{
    Optional<Space> findByIdAndCompanies(Long spaceId, Companies companies);


}

