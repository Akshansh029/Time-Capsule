package com.akshansh.timecapsulebackend.exception;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String msg){
        super(msg);
    }
}
