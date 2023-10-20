package com.example.fileUpload.controller;

import com.example.fileUpload.message.PostDeleteMessage;

import com.example.fileUpload.model.TokenDto;
import com.example.fileUpload.model.User.LoginDto;
import com.example.fileUpload.model.User.UserDto;

import com.example.fileUpload.repository.Entity.User;
import com.example.fileUpload.service.serviceImpl.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> authorize(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userService.login(loginDto));
    }

    @PostMapping("/join")
    public ResponseEntity<PostDeleteMessage> join(@RequestBody UserDto userDto){
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();
        try{
            userService.join(userDto);

        }catch(Exception e){
            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(postDeleteMessage);
    }

    /*@PostMapping("/reissue")
    public ResponseEntity<Object> reissue(@RequestBody TokenDto tokenDto) {
        PostDeleteMessage postDeleteMessage = new PostDeleteMessage();

        try{

            return ResponseEntity.ok( userService.reissue(tokenDto));
        }catch (RuntimeException e){
            ExceptionUtils.getStackTrace(e);
            postDeleteMessage.setMessage(e.getMessage());

        }
        return ResponseEntity.ok(postDeleteMessage);
    }*/
/*    @PostMapping("/logout")
    public ResponseEntity<> logout(){

        return ResponseEntity.ok().build();
    }*/
}
