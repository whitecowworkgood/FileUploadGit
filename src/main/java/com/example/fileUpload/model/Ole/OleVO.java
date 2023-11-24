package com.example.fileUpload.model.Ole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OleVO {

    private String UUIDFileName;
    private String originalFileName;

    @Override
    public String toString() {
        return "{\n" +
                "\t\t\t\"originalFileName\": \"" + originalFileName + "\",\n" +
                "\t\t\t\"uuidfileName\": \"" + UUIDFileName + "\"\n" +
                "\t\t}";
    }

}
