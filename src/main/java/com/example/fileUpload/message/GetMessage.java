package com.example.fileUpload.message;

import lombok.Data;

@Data
public class GetMessage {

    private String message;
    //private int httpStatus;
    private Object data;

    public GetMessage() {
        this.data = null;
        this.message = "NO_CONTENT";
        //this.httpStatus = 204;
    }
}
