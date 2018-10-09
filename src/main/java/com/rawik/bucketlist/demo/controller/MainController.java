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
}
