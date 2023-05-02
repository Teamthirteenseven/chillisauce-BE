package com.example.chillisauce.security;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails, Serializable {

//    private final User user;
//    private final String username;

    /* 테스트 ServiceImpl에서 리턴으로 데이터가 넘어오기 때문에 이쪽에서 수정해준다.*/
    private User user;
    private String username;
    /* 테스트 ServiceImpl에서 리턴으로 데이터가 넘어오기 때문에 이쪽에서 수정해준다.*/

//    public UserDetailsImpl(User user, String username) {
//        this.user = user;
//        this.username = username;
//    }
//
//    public User getUser() {
//        return user;
//    }

    @JsonSerialize(contentUsing = GrantedAuthoritySerializer.class)
    @JsonDeserialize(contentUsing = GrantedAuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }



    @JsonIgnore
    @Override
    public String getUsername() {
        return this.username;
    }
    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return false;
    }

}