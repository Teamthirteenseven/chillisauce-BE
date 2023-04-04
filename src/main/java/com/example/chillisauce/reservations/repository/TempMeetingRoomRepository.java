package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.reservations.entity.TempMeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempMeetingRoomRepository extends JpaRepository<TempMeetingRoom, Long> {
}
