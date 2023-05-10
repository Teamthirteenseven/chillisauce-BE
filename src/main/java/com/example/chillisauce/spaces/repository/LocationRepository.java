package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long>{

    @Query("select l from Location l " +
            "left join Space s " +
            "on l.space.id=s.id " +
            "left join Companies c " +
            "on s.companies.id = c.id " +
            "where l.id= :locationId and c.companyName = :companyName")
    Optional<Location> findByIdAndCompanyName(@Param("locationId") Long locationId,
                                              @Param("companyName") String companyName);

}

