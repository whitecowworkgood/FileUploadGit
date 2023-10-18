package com.example.fileUpload.model.User;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Data
public class LoginDto {

    private String userAccount;
    private String password;
}
