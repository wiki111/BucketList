package com.rawik.bucketlist.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomepageController {

    @GetMapping({"/", "/homepage"})
    public String getHome(){
        return "homepage";
    }

}
