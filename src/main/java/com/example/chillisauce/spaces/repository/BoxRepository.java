package com.example.chillisauce.spaces.repository;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoxRepository extends JpaRepository<Box, Long> {
    Optional<Box> findByIdAndSpaceCompanies(Long boxId, Companies companies);

    @Query("SELECT ul.location, ul FROM UserLocation ul")
    List<Object[]> findAllLocationsWithUserLocations(); //유저 관련된 위치정보를 조회 Userlocation 객체에 해당하는 location 객체랑 같이 object[] 를 반환
}
