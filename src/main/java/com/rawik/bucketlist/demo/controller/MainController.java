package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class MainController {

    @Autowired
    UserService service;

    @GetMapping({"/", "/homepage"})
    public String getHome(){
        return "homepage";
    }

    @GetMapping("/login")
    public String getLogin(){
        return "user/login";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "user/register";
    }

    @GetMapping("/user/successLogin")
    public String getSuccesLoginPage(){
        return "user/successLogin";
    }

    @PostMapping(value = "/register")
    public ModelAndView registerNewUser(
            @ModelAttribute("user") @Valid UserDto userAccount,
            BindingResult result, WebRequest request, Errors errors){

        User registered = new User();

        if(!result.hasErrors()){
            registered = createUserAccount(userAccount, result);
        }

        if(registered == null){
            result.rejectValue("email", "message.regError");
        }

        if(result.hasErrors()){
            return new ModelAndView("/user/register", "user", userAccount);
        }else {
            return new ModelAndView("/user/registerSuccess", "user", userAccount);
        }

    }

    private User createUserAccount(UserDto accountDto, BindingResult result){
        User registered = null;
        try{
            registered = service.registerNewUser(accountDto);
        }catch (EmailExistsException e){
            return null;
        }

        return registered;
    }

    @GetMapping("/profile")
    public String getProfile(){
        return "user/profile";
    }

}
















































