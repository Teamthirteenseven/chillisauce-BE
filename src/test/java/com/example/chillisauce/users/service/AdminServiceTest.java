package com.example.chillisauce.users.service;

import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
        @DisplayName("사원 선택 조회")
        @Test
        void success1() {

        }

        @DisplayName("사원 목록 전체 조회")
        @Test
        void success2() {

        }

    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
        @DisplayName("사원 선택 조회 실패(관리자 권한 없음)")
        @Test
        void fail1() {

        }

        @DisplayName("사원 선택 조회 실패(등록된 사원 없음)")
        @Test
        void fail2() {

        }

        @DisplayName("사원 목록 조회 실패(관리자 권한 없음)")
        @Test
        void fail3() {

        }

        @DisplayName("사원 목록 조회 실패(사원 목록 없음)")
        @Test
        void fail4() {

        }
    }
}