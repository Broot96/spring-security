package com.example.springsecurity.service;

import com.example.springsecurity.constant.CustomUserDetails;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private  final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        return new CustomUserDetails(member);
    }
}
