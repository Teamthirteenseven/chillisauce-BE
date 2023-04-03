package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
}
