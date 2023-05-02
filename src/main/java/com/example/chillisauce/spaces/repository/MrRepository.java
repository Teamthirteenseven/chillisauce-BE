package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MrRepository extends JpaRepository <Mr, Long>{

    Optional<Mr> findByIdAndSpaceCompanies(Long mrId, Companies companies);

    @Query("SELECT m FROM Mr m WHERE m.space.companies.id = :companiesId")
    List<Mr> findAllByCompaniesId(@Param("companiesId") Long companiesId);

    @Query("SELECT m FROM Mr m WHERE m.space.floor = :floor")
    List<Mr> findByFloor(@Param("floor")Floor floor);

}
