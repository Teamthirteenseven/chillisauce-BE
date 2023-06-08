package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserLocation> findByUserId(Long id);
    Optional<UserLocation> findByLocationId(Long locationId);

}
