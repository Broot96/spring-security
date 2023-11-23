package com.example.springsecurity.constant;

import com.example.springsecurity.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails, Serializable {

    private Member member;
    private Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Map<String, Objects> attributes;

    public CustomUserDetails(Member member) {this.member = member;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.member.getRole().getKey()));
        return authorities;
    }

    @Override
    public String getPassword() {return member.getPassword();}

    @Override
    public String getUsername() {return member.getUserId();}

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
