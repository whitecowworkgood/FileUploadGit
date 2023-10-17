package com.example.fileUpload.model.members;

import com.drew.lang.annotations.NotNull;
import com.example.fileUpload.util.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    private Long id;

    @NotNull
    @Size(min = 3, max = 12)
    private String username;

    @NotNull
    @Size(min = 3, max = 20)
    private String password;

    @NotNull
    @Size(min = 3, max = 12)
    private String nickname;

    @NotNull
    @Size(min = 3, max = 30)
    private String email;

    @NotNull
    @Size(min = 3, max = 12)
    private String realName;

    @NotNull
    private Role roles;

    @JsonIgnore
    private boolean activated;
}
