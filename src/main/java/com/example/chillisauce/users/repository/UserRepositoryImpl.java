package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.QCompanies;
import com.example.chillisauce.users.entity.QUser;
import com.example.chillisauce.users.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.example.chillisauce.users.entity.QUser.user;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositorySupport{
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    public UserRepositoryImpl(EntityManager em) {
        super(User.class);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    QUser user = QUser.user;
    QCompanies company = QCompanies.companies;
    public static final Long MAX_SEARCH_RESULTS = 5L;

    @Override
    public Optional<User> findByIdAndCompanies_CompanyName(Long id, String companyName) {

        User result = queryFactory
                .selectFrom(user)
                .join(user.companies, company)
                .where(user.id.eq(id).and(companyNameEquals(companyName)))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<User> findAllByCompanies_CompanyName(String companyName) {
        return queryFactory
                .selectFrom(user)
                .join(user.companies, company)
                .where(user.id.eq(user.id).and(companyNameEquals(companyName)))
                .fetch();
    }

    @Override
    public List<User> findAllByUsernameContainingAndCompanies(String name, String companyName) {
        return queryFactory
                .selectFrom(user)
                .join(user.companies)
                .fetchJoin()
                .where(user.username.like("%" + name + "%").and(companyNameEquals(companyName)))
                .limit(MAX_SEARCH_RESULTS)
                .fetch();
    }

    private BooleanExpression companyNameEquals(String companyName) {
        return user.companies.companyName.eq(companyName);
    }
}
