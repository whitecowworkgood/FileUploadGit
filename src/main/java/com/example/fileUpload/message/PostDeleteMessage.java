package com.example.fileUpload.message;

import lombok.Data;

@Data
public class PostDeleteMessage {

    private String message;
    //private int httpStatus;

    public PostDeleteMessage() {
        this.message = "UNPROCESSABLE_ENTITY";
        //this.httpStatus = 422;
    }
}
