package com.example.chillisauce.fixture;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;

import java.time.LocalDateTime;

public class FixtureFactory {
    public static Companies Company_생성() {
        return Companies.builder()
                .companyName("testCompany")
                .certification("testCert")
                .build();
    }

    public static User User_USER권한_생성(Companies company) {
        return User.builder()
                .companies(company)
                .email("test@test.com")
                .password("12345678")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
    }

    public static User User_USER권한_생성_아이디지정(Long id, Companies company) {
        return User.builder()
                .id(id)
                .companies(company)
                .email("test@test.com")
                .password("12345678")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
    }

    public static User User_USER권한_생성_아이디_이메일_지정(Long id, Companies company, String email) {
        return User.builder()
                .id(id)
                .companies(company)
                .email(email)
                .password("12345678")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
    }

    public static User User_ADMIN권한_생성(Companies company, String email) {
        return User.builder()
                .companies(company)
                .email(email)
                .password("12345678")
                .username("testAdminUser")
                .role(UserRoleEnum.ADMIN)
                .build();
    }

    public static Mr MeetingRoom_생성() {
        return Mr.builder()
                .locationName("testMeetingRoom")
                .x("150").y("200")
                .build();
    }

    public static Mr MeetingRoom_생성_아이디_지정(Long id) {
        return Mr.builder()
                .id(id)
                .locationName("testMeetingRoom")
                .x("150").y("200")
                .build();
    }

    public static Reservation Reservation_생성(User user, Mr mr, LocalDateTime start, LocalDateTime end) {
        return Reservation.builder()
                .user(user)
                .meetingRoom(mr)
                .startTime(start)
                .endTime(end)
                .build();
    }

    public static Reservation Reservation_생성_아이디_지정(Long id, User user, Mr mr, LocalDateTime start, LocalDateTime end) {
        return Reservation.builder()
                .id(id)
                .user(user)
                .meetingRoom(mr)
                .startTime(start)
                .endTime(end)
                .build();
    }
}
