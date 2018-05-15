package com.rawik.bucketlist.demo.exceptions;

public class EmailExistsException extends Throwable {
    public EmailExistsException(final String message){
        super(message);
    }
}
