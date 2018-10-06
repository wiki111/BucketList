package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.exceptions.NotAuthorizedException;
import com.rawik.bucketlist.demo.exceptions.NotFoundException;
import com.rawik.bucketlist.demo.exceptions.OperationException;
import com.rawik.bucketlist.demo.exceptions.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @GetMapping({"/", "/homepage"})
    public String getHome(){
        return "homepage";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception e){
        return prepareModelAndView(e, "not-found-error");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(StorageException.class)
    public ModelAndView handleStorage(Exception e){
        return prepareModelAndView(e, "operation-error");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OperationException.class)
    public ModelAndView handleOperation(Exception e){
        return prepareModelAndView(e, "operation-error");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedException.class)
    public ModelAndView handleNotAuthorized(Exception e){
        return prepareModelAndView(e,"not-authorized-error");
    }

    private ModelAndView prepareModelAndView(Exception e, String viewName){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", e.getMessage());
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

}
