package com.example.fileUpload.controller;

import com.example.fileUpload.message.PostDeleteMessage;
import com.example.fileUpload.model.Member.MemberRequestDto;
import com.example.fileUpload.model.Token.TokenRequestDto;
import com.example.fileUpload.service.serviceImpl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "jwt 토큰 관리 컨트롤러")
public class AuthController {
    private final AuthService authService;
    @Operation(summary = "jwt 회원가입", description = "회원가입을 수행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody MemberRequestDto memberRequestDto) {
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        try{
            postDeleteMessage.setMessage(authService.signup(memberRequestDto));

        }catch (RuntimeException e){
            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage(e.getMessage());
        }
        return ResponseEntity.ok(postDeleteMessage);
    }
    @Operation(summary = "Login", description = "로그인을 수행하고, jwt 토큰을 발급 받습니다.")
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody MemberRequestDto memberRequestDto) {
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();
        try{
            return ResponseEntity.ok(authService.login(memberRequestDto));
        }catch (RuntimeException e){

            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage(e.getMessage());
            return ResponseEntity.ok(postDeleteMessage);
        }

    }
    @Operation(summary = "Reissue", description = "기간이 만료된 accessToken을 재발급 합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<PostDeleteMessage> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        try{
            postDeleteMessage.setMessage(authService.reissue(tokenRequestDto));
            return ResponseEntity.ok(postDeleteMessage);


        }catch (RuntimeException e){

            postDeleteMessage.setMessage(e.getMessage());
            return ResponseEntity.ok().body(postDeleteMessage);

        }

    }
    @Operation(summary = "Logout", description = "사용자 정보를 로그아웃 시킵니다..")
    @PostMapping("/logout")
    public ResponseEntity<PostDeleteMessage> logout(@RequestBody TokenRequestDto tokenRequestDto) {
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        try{
            postDeleteMessage.setMessage(authService.logout(tokenRequestDto));
            return ResponseEntity.ok(postDeleteMessage);

        }catch (RuntimeException e){

            postDeleteMessage.setMessage(e.getMessage());
            return ResponseEntity.ok().body(postDeleteMessage);

        }
    }

}
