package com.example.fileUpload.model.Token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken {

    private String key;
    private String value;
    private String signingKey;

    @Builder
    public RefreshToken(String key, String value, String signingKey) {
        this.key = key;
        this.value = value;
        this.signingKey=signingKey;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}
