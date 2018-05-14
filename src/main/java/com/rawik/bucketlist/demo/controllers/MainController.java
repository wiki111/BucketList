package com.rawik.bucketlist.demo.controllers;

import com.rawik.bucketlist.demo.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;


@Controller
public class MainController {

    @GetMapping({"/", "/homepage"})
    public String getHome(){
        return "homepage";
    }

    @GetMapping("/login")
    public String getLogin(){
        return "user/login";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model){
        UserDTO userDTO = new UserDTO();
        model.addAttribute("user", userDTO);
        return "user/register";
    }


}
