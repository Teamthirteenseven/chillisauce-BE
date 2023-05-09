package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.UserLocation;
import com.example.chillisauce.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserLocation> findByUserId(Long id);
    Optional<UserLocation> findByLocationId(Long locationId);

}
