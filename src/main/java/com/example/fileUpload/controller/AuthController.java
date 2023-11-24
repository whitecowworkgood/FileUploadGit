package com.example.fileUpload.controller;

import com.example.fileUpload.message.ResultMessage;
import com.example.fileUpload.model.Member.MemberRequestDto;
import com.example.fileUpload.model.Token.TokenRequestDto;
import com.example.fileUpload.service.serviceImpl.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "jwt 토큰 관리 컨트롤러")
public class AuthController {
    private final AuthService authService;
    @Operation(summary = "jwt 회원가입", description = "회원가입을 수행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Object> signup( @Valid @RequestBody MemberRequestDto memberRequestDto) {

        return ResponseEntity.ok(authService.signup(memberRequestDto));
    }

    @Operation(summary = "Login", description = "로그인을 수행하고, jwt 토큰을 발급 받습니다.")
    @PostMapping("/login")
    public ResponseEntity<Object> login( @Valid @RequestBody MemberRequestDto memberRequestDto) {

        return ResponseEntity.ok(authService.login(memberRequestDto));

    }
    @Operation(summary = "Reissue", description = "기간이 만료된 accessToken을 재발급 합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<Object> reissue(@Valid @RequestBody TokenRequestDto tokenRequestDto) throws JsonProcessingException {

        return ResponseEntity.ok(authService.reissue(tokenRequestDto));

    }
    @Operation(summary = "Logout", description = "사용자 정보를 로그아웃 시킵니다..")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody TokenRequestDto tokenRequestDto) {

        String authMessage = authService.logout(tokenRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResultMessage.getInstance().logoutOf(authMessage));
    }

}
