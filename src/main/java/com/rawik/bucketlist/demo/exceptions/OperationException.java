package com.rawik.bucketlist.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OperationException extends RuntimeException{

    public OperationException(){
        super();
    }

    public OperationException(String message){
        super(message);
    }

    public OperationException(String message, Throwable cause){
        super(message, cause);
    }

}
