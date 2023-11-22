package com.example.springsecurity.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.print.attribute.standard.MediaSize;

@Entity
@Table(name = "REFRESH_TOKEN")
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id
    @Column(name = "MEMBER_USER_NO")
    private Long no;

    @OneToOne
    @MapsId
    @JoinColumn(name = "MEMBER_USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column
    private String value;

    @Builder
    public RefreshToken(Member member, String value){
        this.member = member;
        this.value = value;
    }

    public RefreshToken updateValue(String token){
        this.value = token;
        return this;
    }
}
