package com.example.springsecurity.service;

import com.example.springsecurity.constant.Role;
import com.example.springsecurity.dto.TokenDTO;
import com.example.springsecurity.dto.MemberDTO;
import com.example.springsecurity.dto.LoginDTO;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.jwt.TokenStatus;
import com.example.springsecurity.repository.MemberRepository;
import com.example.springsecurity.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Member signup(MemberDTO memberDTO) {
        if(memberRepository.findByUserId(memberDTO.getUserId()).orElse(null) != null) {
            throw new RuntimeException("이미 존재하는 Id입니다.");
        }
        Member member = Member.builder()
                .userId(memberDTO.getUserId())
                .email(memberDTO.getEmail())
                .name(memberDTO.getName())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .role(Role.TEMP)
                .build();
        return memberRepository.save(member);
    }

    public HashMap<String, Object> signin(LoginDTO loginDTO) throws EntityNotFoundException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDTO tokenDTO = tokenProvider.createToken(authentication);
        Member member = memberRepository.findByUserId(loginDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
        RefreshToken refreshToken = refreshTokenRepository.findById(member.getMemberNo())
                .orElse(null);
        if(refreshToken == null) {
            RefreshToken rt = RefreshToken.builder()
                    .member(member)
                    .value(tokenDTO.getRefreshToken())
                    .build();
            refreshTokenRepository.save(rt);
        } else {
            refreshToken.updateValue(tokenDTO.getRefreshToken());
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("tokenDTO", tokenDTO);
        result.put("memberId", member.getUserId());
        result.put("role", member.getRole());
        return result;
    }

    public TokenDTO refresh(TokenDTO tokenDTO) throws RuntimeException {
        TokenStatus.StatusCode tokenStatusCode = tokenProvider.validateToken(tokenDTO.getRefreshToken());
        if(tokenStatusCode != TokenStatus.StatusCode.OK)
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        Authentication authentication = tokenProvider.getAuthentication(tokenDTO.getAccessToken());
        Member member = memberRepository.findByUserId(authentication.getName()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
        RefreshToken refreshToken = refreshTokenRepository.findById(member.getMemberNo())
                .orElseThrow(EntityNotFoundException::new);
        if(!refreshToken.getValue().equals(tokenDTO.getRefreshToken()))
            throw new RuntimeException("토큰의 유저 정보와 일치하지 않습니다.");
        TokenDTO newToken = tokenProvider.createToken(authentication);
        refreshToken.updateValue(newToken.getRefreshToken());
        return newToken;
    }
}
