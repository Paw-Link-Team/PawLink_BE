package com.gdg.backend.global.exception;

public class ProfileImageUploadException extends RuntimeException{
    public ProfileImageUploadException(String message, Throwable throwable){
        super(message, throwable);
    }
}
