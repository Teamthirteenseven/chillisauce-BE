package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long>, LocationRepositorySupport{

    Optional<Location> findByIdAndSpaceCompanies(Long locationId, Companies companies);

}

