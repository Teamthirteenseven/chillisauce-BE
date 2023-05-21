package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long>, FloorRepositorySupport{
    Optional<Floor> findByIdAndCompanies(Long floorId, Companies company);

}
