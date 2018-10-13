package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.exceptions.NotAuthorizedException;
import com.rawik.bucketlist.demo.exceptions.NotFoundException;
import com.rawik.bucketlist.demo.exceptions.OperationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public void handleError(HttpServletRequest request){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        String message = "Error occured ( status : " + statusCode + " ). Details : ";
        if(exception != null){
            if(exception.getMessage() == null){
                        message += "unknown error.";
            }else {
                message += exception.getMessage();
            }
        }else {
            message += " unknown error.";
        }

        switch (statusCode){
            case 500 :
                throw new OperationException(message);
            case 401 :
                throw new NotAuthorizedException(message);
            case 404 :
                throw new NotFoundException(message);
            default :
                throw new NotFoundException(message);
        }
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
