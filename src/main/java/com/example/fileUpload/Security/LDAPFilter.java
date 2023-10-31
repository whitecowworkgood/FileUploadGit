package com.example.fileUpload.Security;

import com.example.fileUpload.model.Member.MemberRequestDto;

public class LDAPFilter {

    public static void MemberRequestDtoLDAPFilter(MemberRequestDto memberRequestDto) {
        if (!memberRequestDto.getAccount().matches("[\\w\\s]*") || !memberRequestDto.getPassword().matches("[\\w\\s!@#$%^&*()_+-=]*")) {
            throw new RuntimeException("잘못된 값을 입력하였습니다.");
        }
    }
}

