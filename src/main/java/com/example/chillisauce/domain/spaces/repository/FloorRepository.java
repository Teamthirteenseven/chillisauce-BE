package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.entity.Floor;
import com.example.chillisauce.domain.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long>, FloorRepositorySupport{
    Optional<Floor> findByIdAndCompanies(Long floorId, Companies company);

}
