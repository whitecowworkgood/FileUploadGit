package com.example.fileUpload.model.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {

    private boolean message;

    public static MemberResponseDto of(boolean result){
        return new MemberResponseDto(result);
    }
}
