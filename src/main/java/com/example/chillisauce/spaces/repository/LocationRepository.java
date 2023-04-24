package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

}

