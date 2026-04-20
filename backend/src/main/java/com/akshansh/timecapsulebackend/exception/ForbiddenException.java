package com.akshansh.timecapsulebackend.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String msg){
        super(msg);
    }
}
