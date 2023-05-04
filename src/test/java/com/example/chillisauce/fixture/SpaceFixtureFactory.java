package com.example.chillisauce.fixture;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;

import java.util.ArrayList;
import java.util.List;

public class SpaceFixtureFactory {

    public static Companies Company_생성() {
        return Companies.builder()
                .companyName("testCompany")
                .certification("testCert")
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
                .build();
    }

    public static Space Space_생성_아이디_지정(Long id) {
        return Space.builder()
                .id(id)
                .spaceName("testSpace")
                .build();
    }


    public static Box Box_생성() {
        return Box.builder()
                .locationName("testBox")
                .x("777").y("777")
                .build();
    }

    public static Mr Mr_생성() {
        return Mr.builder()
                .locationName("testMr")
                .x("111").y("222")
                .build();
    }

    public static Mr Mr_생성_아이디_지정(Long id) {
        return Mr.builder()
                .id(id)
                .locationName("testMr")
                .x("111").y("222")
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


    public static MultiBox MultiBox_생성() {
        return MultiBox.builder()
                .locationName("testMultiBox")
                .x("222").y("200")
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


    public static Reservation Reservation_생성() {
        return Reservation.builder()
                .build();
    }
}
