package com.example.fileUpload.message;

import com.example.fileUpload.model.File.FileVO;
import com.example.fileUpload.model.File.UserFileVO;
import com.example.fileUpload.model.Ole.OleVO;

import java.util.List;

public class ResultMessage {

    private static class LazyHolder {
        private static final ResultMessage instance = new ResultMessage();
    }

    private volatile String GET_RESULT_FORMAT = "{\n" +
            "\t\"ResultMessage\": \"%s\",\n" +
            "\t\"data\": %s\n" +
            "}";

    private volatile String POST_RESULT_FORMAT = "{\n" +
            "\t\"ResultMessage\": \"%s\",\n" +
            "}";

    private volatile String JWT_ERROR_MESSAGE = "{\n" +
            "\t\"ErrorCode\": \"%s\",\n" +
            "\t\"Message\": \"%s\"\n" +
            "}";

    private ResultMessage() {
    }

    public static ResultMessage getInstance() {
        return LazyHolder.instance;
    }


    public String fileListOf(String fileListMessage,List<FileVO> fileVOS) {
        return String.format(GET_RESULT_FORMAT, fileListMessage, fileVOS != null ? fileVOS.toString() : "Empty_Data");

    }
    public String oleListOf(String oleListMessage,  List<OleVO> oleVOS) {
        return String.format(GET_RESULT_FORMAT, oleListMessage, oleVOS != null ? oleVOS.toString() : "Empty_Data");
    }
    public String userFileOf(String userFilesMessage, List<UserFileVO> userFileVOS) {

        return String.format(GET_RESULT_FORMAT, userFilesMessage, userFileVOS != null ? userFileVOS.toString() : "Empty_Data");
    }

    public String acceptOf(String acceptMessage){
        return String.format(POST_RESULT_FORMAT, acceptMessage);
    }

    public String logoutOf(String logoutMessage){
        return String.format(POST_RESULT_FORMAT, logoutMessage);
    }
    public String fileUploadOf(String fileUploadMessage){
        return String.format(POST_RESULT_FORMAT, fileUploadMessage);
    }

    public String jwtError(String code, String message){
        return String.format(JWT_ERROR_MESSAGE, code, message);
    }

}
