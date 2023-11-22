package com.example.springsecurity.entity;

import com.example.springsecurity.constant.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@Slf4j
public class Member extends BaseTimeEntity{

    @Id
    @Column(name = "MEMBER_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @Builder
    Member(String userId, String email, String password, String name, Role role){
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}
