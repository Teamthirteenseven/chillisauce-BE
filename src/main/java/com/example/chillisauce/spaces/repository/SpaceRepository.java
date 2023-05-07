package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long>, SpaceRepositorySupport{
    Optional<Space> findByIdAndCompanies(Long spaceId, Companies companies);


    List<Space> findAllByCompaniesId(Long spaceId);

}

