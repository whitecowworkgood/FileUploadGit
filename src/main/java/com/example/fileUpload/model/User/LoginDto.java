package com.example.fileUpload.model.User;

import com.drew.lang.annotations.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotNull
    @Size(min = 4, max = 20)
    private String userAccount;

    @NotNull
    @Size(min = 4, max = 30)
    private String password;
}
