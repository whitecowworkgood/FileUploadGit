package com.example.fileUpload.model.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Member {
    private Long id;
    private String account;
    private String password;
    private Authority authority;

    @Builder
    public Member(String account, String password, Authority authority) {
        this.account = account;
        this.password = password;
        this.authority = authority;
    }
}
