package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MrRepository extends JpaRepository <Mr, Long>{
    /**
     *  개선 전
     */
    Optional<Mr> findByIdAndSpaceCompanies(Long mrId, Companies companies);

    @Query("SELECT m FROM Mr m WHERE m.space.floor = :floor")
    List<Mr> findByFloor(@Param("floor")Floor floor);

    @Query("SELECT m FROM Mr m LEFT JOIN FETCH m.reservations r JOIN m.space s WHERE s.companies.id = :companiesId")
    List<Mr> findAllByCompaniesId (@Param("companiesId") Long companiesId);

    /**
     *  1차 개선
     */
    @Modifying
    @Query("UPDATE Reservation r SET r.meetingRoom = NULL WHERE r.meetingRoom.id = :mrId")
    void clearReservationsForMeetingRoom(@Param("mrId") Long mrId);

    @Modifying
    @Query("UPDATE Reservation r SET r.meetingRoom = NULL WHERE r.meetingRoom.id IN (SELECT m.id FROM Mr m WHERE m.space.id = :spaceId)")
    void clearAllReservationsForSpace(@Param("spaceId") Long spaceId);

    @Modifying
    @Query("UPDATE Reservation r SET r.meetingRoom = NULL WHERE r.meetingRoom.id IN (SELECT m.id FROM Mr m WHERE m.space.floor.id = :floorId)")
    void clearAllReservationsForFloor(@Param("floorId") Long floorId);
}
