package com.example.springsecurity.controller;

import com.example.springsecurity.dto.LoginDTO;
import com.example.springsecurity.dto.MemberDTO;
import com.example.springsecurity.dto.TokenDTO;
import com.example.springsecurity.jwt.JwtFilter;
import com.example.springsecurity.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/singup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberDTO memberDTO){

        try {
            authService.signup(memberDTO);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorize(@Valid LoginDTO loginDTO){
        try{
            HashMap<String, Object> result = authService.signin(loginDTO);
            TokenDTO tokenDTO = (TokenDTO) (result.get("tokenDTO"));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,
                            "Bearer "+tokenDTO.getAccessToken());
            return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>("아이디 혹은 비밀번호가 틀렸습니다.", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
}
