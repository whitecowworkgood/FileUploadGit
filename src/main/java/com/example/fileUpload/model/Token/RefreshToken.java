package com.example.fileUpload.model.Token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken {

    private String key;
    private String value;
    private String signatureKey;

    @Builder
    public RefreshToken(String key, String value, String signKey) {
        this.key = key;
        this.value = value;
        this.signatureKey=signKey;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}
