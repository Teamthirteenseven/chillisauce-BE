package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositorySupport{
    Optional<User> findByEmail (String email);

//    List<User> findAllByCompanies_CompanyName (String companyName);
//    Optional<User> findByIdAndCompanies_CompanyName (Long id, String companyName);

    List<User> findAllByIdInAndCompanies_CompanyName(List<Long> userIds, String companyName);

//    @Query("select u " +
//            "from users u " +
//            "join fetch u.companies " +
//            "where u.username like %:name%")
//    List<User> findAllByUsernameContainingAndCompanies(@Param("name") String name);
}
