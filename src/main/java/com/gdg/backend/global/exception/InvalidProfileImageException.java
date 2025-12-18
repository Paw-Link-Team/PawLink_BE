package com.gdg.backend.global.exception;

public class InvalidProfileImageException extends RuntimeException{
    public InvalidProfileImageException(String message){
        super(message);
    }
}
