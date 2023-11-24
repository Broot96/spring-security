package com.example.springsecurity.dto;

import com.example.springsecurity.entity.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long memberNo;

    @NotNull
    private String userId;

    @NotNull
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;
    private String role;
    private String regTime;
    private String updateTime;

    private static ModelMapper modelMapper = new ModelMapper();

    public static MemberDTO of(Member member) {
        return modelMapper.map(member, MemberDTO.class);
    }
}
