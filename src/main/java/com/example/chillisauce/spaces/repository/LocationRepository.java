package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
