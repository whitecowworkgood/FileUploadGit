package com.example.fileUpload.model.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {

    private String account;

    public static MemberResponseDto of(Member member){
        return new MemberResponseDto(member.getAccount());
    }
}
