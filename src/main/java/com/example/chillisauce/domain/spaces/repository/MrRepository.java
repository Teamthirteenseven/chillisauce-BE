package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.entity.Mr;
import com.example.chillisauce.domain.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MrRepository extends JpaRepository <Mr, Long> {

    Optional<Mr> findByIdAndSpaceCompanies(Long mrId, Companies companies);

    @Query("SELECT m FROM Mr m LEFT JOIN FETCH m.reservations r JOIN m.space s WHERE s.companies.id = :companiesId")
    List<Mr> findAllByCompaniesId(@Param("companiesId") Long companiesId);
}