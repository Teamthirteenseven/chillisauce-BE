package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long>, SpaceRepositorySupport{
    Optional<Space> findByIdAndCompanies(Long spaceId, Companies companies);
    List<SpaceResponseDto> getSpacesWithLocations(Long spaceId);
    List<SpaceResponseDto> getSpaceAllList(String companyName);

    List<Space> findAllByCompaniesId(Long spaceId);

}

