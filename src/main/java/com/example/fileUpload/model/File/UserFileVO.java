package com.example.fileUpload.model.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserFileVO {

    private Long fileSize;
    private String originalFileName;
    private Long countNum;
    private String comment;
    private String timeStamp;
    @JsonIgnore
    private boolean isEncrypt;
    @Override
    public String toString() {
        return "{\n" +
                "\t\t\t\"fileSize\": " + fileSize + ",\n" +
                "\t\t\t\"originalFileName\": \"" + originalFileName + "\",\n" +
                "\t\t\t\"countNum\": " + countNum + ",\n" +
                "\t\t\t\"comment\": \"" + (comment != null ? comment : "null") + "\",\n" +
                "\t\t\t\"timeStamp\": \"" + timeStamp + "\"\n" +
                "\t\t}";
    }

}
