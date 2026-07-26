package com.javaspringboot_tutorial.security.dto.response;

public class ResponseError extends ResponseData{
    public ResponseError(int status, String message){
        super(status, message);
    }

}
