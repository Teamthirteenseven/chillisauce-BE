package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    Optional<UserLocation> findByUserId(Long id);
    Optional<UserLocation> findByLocationId(Long locationId);

}
