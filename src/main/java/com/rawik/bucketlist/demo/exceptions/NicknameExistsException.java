package com.rawik.bucketlist.demo.exceptions;

public class NicknameExistsException extends Throwable{
    public NicknameExistsException(final String message){
        super(message);
    }
}
