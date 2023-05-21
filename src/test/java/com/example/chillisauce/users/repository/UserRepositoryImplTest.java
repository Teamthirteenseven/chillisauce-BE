package com.example.chillisauce.users.repository;

import com.example.chillisauce.config.TestConfig;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.QCompanies;
import com.example.chillisauce.users.entity.QUser;
import com.example.chillisauce.users.entity.User;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
@Nested
@DisplayName("유저 레포지토리 테스트")
class UserRepositoryImplTest {

    @Autowired
    UserRepositoryImpl userRepositoryImpl;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    EntityManager em;
    private final Companies company = Company_생성();
    private final User user1 = User_USER권한_생성_이메일_이름_지정(company, "test1@test1.com", "차범근");
    private final User user2 = User_USER권한_생성_이메일_이름_지정(company, "test2@test2.com", "강백호");
    private final User user3 = User_USER권한_생성_이메일_이름_지정(company, "test3@test3.com", "차두리");
    private final User user4 = User_USER권한_생성_이메일_이름_지정(company, "test4@test4.com", "전일보");
    private final User user5 = User_USER권한_생성_이메일_이름_지정(company, "test5@test5.com", "윤대협");
    private final User user6 = User_USER권한_생성_이메일_이름_지정(company, "test6@test6.com", "손흥민");
//    private final User user7 = User_USER권한_생성_아이디_이메일_이름_지정(7L, company, "test7@test7.com", "박지성");
//    private final User user8 = User_USER권한_생성_아이디_이메일_이름_지정(8L, company, "test8@test8.com", "이강인");
//    private final User user9 = User_USER권한_생성_아이디_이메일_이름_지정(9L, company, "test9@test9.com", "홍서방");
//    private final User user10 = User_USER권한_생성_아이디_이메일_이름_지정(10L, company, "test10@test10.com", "홍길동");
    QUser qUser = QUser.user;
    QCompanies qcompany = QCompanies.companies;
    public static final Long MAX_SEARCH_RESULTS = 5L;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
        @Test
        void 회원_선택_조회() {
            //given
            companyRepository.save(company);
            userRepository.save(user1);
            //when
            Optional<User> result = userRepository.findByIdAndCompanies_CompanyName(user1.getId(), user1.getCompanies().getCompanyName());

            //then
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getCompanies().getCompanyName()).isEqualTo(company.getCompanyName());

        }

        @Test
        void 회원_전체_조회() {
            //given
            List<User> userList = Stream.of(user2, user3, user4).toList();
            companyRepository.save(company);
            userRepository.saveAll(userList);
            //when
            List<User> result = userRepository.findAllByCompanies_CompanyName(company.getCompanyName());
            //then
            assertThat(result.size()).isEqualTo(3);
        }

        @Test
        void 회원_검색() {
            //given
            String searchName = "차";
            List<User> userList = Stream.of(user1, user2, user3, user4, user5, user6).toList();
            companyRepository.save(company);
            userRepository.saveAll(userList);
            for (User user : userRepository.findAll()) {
                System.out.println(user.getId() + user.getUsername());
            }
            //when
            List<User> result = userRepository.findAllByUsernameContainingAndCompanies(searchName, company.getCompanyName());
            //then
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(2);
            for (User user : result) {
                assertThat(user.getUsername()).contains(searchName);
                assertThat(user.getCompanies().getCompanyName()).isEqualTo(company.getCompanyName());
            }
        }
    }

}