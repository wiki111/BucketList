package com.rawik.bucketlist.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/", "/homepage"})
    public String getHome(){
        return "homepage";
    }

}
