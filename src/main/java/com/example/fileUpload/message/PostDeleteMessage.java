package com.example.fileUpload.message;

import lombok.Data;

@Data
public class PostDeleteMessage {

    private Object message;

    public PostDeleteMessage() {
        this.message = "CREATE";
    }

}
