package com.example.fileUpload.controller;

import com.example.fileUpload.model.TokenInfo;
import com.example.fileUpload.model.User.LoginDto;
import com.example.fileUpload.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public TokenInfo login(@RequestBody LoginDto loginDto){
        String memberAccount = loginDto.getUserAccount();
        String password = loginDto.getPassword();

        TokenInfo tokenInfo = memberService.login(memberAccount, password);

        return tokenInfo;
    }
}
