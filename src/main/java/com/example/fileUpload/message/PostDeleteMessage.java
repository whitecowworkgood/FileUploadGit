package com.example.fileUpload.message;

import lombok.Data;

@Data
public class PostDeleteMessage {

    private String message;

    public PostDeleteMessage() {
        this.message = "CREATE";
    }

}
