package com.example.fileUpload.model.Token;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {
    @NotEmpty(message = "토큰 값은 빈 값이 될 수 없습니다.")
    @Pattern(regexp = "^[^\\\\+/=,:;\"&<>^']*$", message = "토큰에 사용할 수 없는 특수문자가 포함되어 있습니다.")
    private String accessToken;

    @NotEmpty(message = "토큰 값은 빈 값이 될 수 없습니다.")
    @Pattern(regexp = "^[^\\\\+/=,:;\"&<>^']*$", message = "토큰에 사용할 수 없는 특수문자가 포함되어 있습니다.")
    private String refreshToken;
}
