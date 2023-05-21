package com.example.chillisauce.fixture;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.entity.ReservationUser;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FixtureFactory {
    public static Companies Company_생성() {
        return Companies.builder()
                .companyName("testCompany")
                .certification("testCert")
                .build();
    }

    public static Companies Company_생성_이름_지정(String companyName) {
        return Companies.builder()
                .companyName(companyName)
                .certification("testCert")
                .build();
    }

    public static User User_USER권한_생성(Companies company) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .companies(company)
                .email("test@test.com")
                .password(passwordEncoder.encode("12345678"))
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

    public static User User_USER권한_생성_이메일_이름_지정(Companies company, String email, String username) {
        return User.builder()
                .companies(company)
                .email(email)
                .password("12345678")
                .username(username)
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

    public static ReservationUser ReservationUser_생성(Reservation reservation, User attendee) {
        return ReservationUser.builder()
                .reservation(reservation)
                .attendee(attendee)
                .build();
    }

    public static ReservationUser ReservationUser_생성_아이디_지정(Reservation reservation, User attendee, Long id) {
        return ReservationUser.builder()
                .id(id)
                .reservation(reservation)
                .attendee(attendee)
                .build();
    }

    public static Companies Different_Company_생성() {
        return Companies.builder()
                .companyName("differentCompanyName")
                .build();
    }

    public static Floor Floor_생성_아이디_지정(Long id) {
        return Floor.builder()
                .id(id)
                .floorName("testFloor")
                .spaces(new ArrayList<>())
                .build();
    }
    public static Floor Floor_생성() {
        return Floor.builder()
                .floorName("testFloor")
                .build();
    }

    public static Space Space_생성_아이디_지정(Long id) {
        return Space.builder()
                .id(id)
                .spaceName("testSpace")
                .build();
    }
    public static Space Space_생성_아이디_지정_회사_지정(Long id,Companies companies) {
        return Space.builder()
                .id(id)
                .spaceName("testSpace")
                .companies(companies)
                .build();
    }

    public static Space Space_생성() {
        return Space.builder()
                .spaceName("testSpace")
                .build();
    }





    public static Box Box_생성() {
        return Box.builder()
                .locationName("testBox")
                .x("777").y("777")
                .build();
    }

    public static Box Box_생성_아이디_지정(Long boxId) {
        return Box.builder()
                .id(boxId)
                .locationName("testBox")
                .x("777").y("777")
                .build();
    }

    public static Mr Mr_생성_예약_추가(Reservation reservation) {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        return Mr.builder()
                .locationName("testMr")
                .x("111").y("222")
                .reservation(reservations)
                .build();
    }
    public static Mr Mr_생성() {
        return Mr.builder()
                .locationName("testMr")
                .x("111").y("222")
                .build();
    }


    public static MultiBox MultiBox_생성() {
        return MultiBox.builder()
                .locationName("testMultiBox")
                .x("222").y("200")
                .build();
    }

    public static MultiBox MultiBox_생성_아이디_지정(Long multiBoxId) {
        return MultiBox.builder()
                .id(multiBoxId)
                .locationName("testMultiBox")
                .x("222").y("200")
                .build();
    }

    public static UserLocation UserLocation_생성_Box(Box box, User user) {
        return UserLocation.builder()
                .location(box)
                .userId(user.getId())
                .username("test")
                .build();
    }

    public static UserLocation UserLocation_생성_MultiBox(MultiBox multiBox, User user) {
        return UserLocation.builder()
                .location(multiBox)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
    public static UserLocation UserLocation_생성_Location(Location location, User user) {
        return UserLocation.builder()
                .location(location)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    public static UserDetailsImpl details_권한_USER_유저_네임_NULL(Companies company) {
        return new UserDetailsImpl
                (User.builder().role(UserRoleEnum.USER).companies(company).build(), "null");
    }


    public static UserDetailsImpl details_권한_USER(Companies company) {
        return new UserDetailsImpl
                (User.builder().role(UserRoleEnum.USER).companies(company).build(), "test");
    }

    public static UserDetailsImpl details_권한_ADMIN_유저_네임_NULL(Companies company) {
        return new UserDetailsImpl
                (User.builder().role(UserRoleEnum.ADMIN).companies(company).build(), "null");
    }
    public static Reservation Reservation_생성_빈값() {
        return Reservation.builder().build();
    }
}
