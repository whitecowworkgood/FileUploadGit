package com.example.fileUpload.model.Member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

    @Size(min=5, max=15, message = "계정은 5~15이내로 입력해야 합니다.")
    @Pattern(regexp = "[\\w]*", message = "계정은 특수문자를 포함할 수 없습니다.")
    @NotBlank(message = "빈 값은 입력할 수 없습니다.")
    private String account;

    @Size(min=5, max=20, message = "패스워드는 5~20이내로 입력해야 합니다.")
    @Pattern(regexp = "[\\w!@#$%^&*()_+-=]*", message = "허가되지 않는 특수문자가 포함되어 있습니다.")
    @NotBlank(message = "빈 값은 입력할 수 없습니다.")
    private String password;


    public Member toMember(PasswordEncoder passwordEncoder){
        return Member.builder()
                .account(account)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(){
        return new UsernamePasswordAuthenticationToken(account, password);
    }
}
