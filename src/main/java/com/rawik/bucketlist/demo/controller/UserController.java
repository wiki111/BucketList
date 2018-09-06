package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.mapper.UserMapper;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;


@Controller
public class UserController {

    private UserService service;
    private UserMapper userMapper;

    public UserController(UserService service, UserMapper userMapper) {
        this.service = service;
        this.userMapper = userMapper;
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
            BindingResult result){

        User registered = new User();

        if(!result.hasErrors()){
            registered = createUserAccount(userAccount);
        }

        if(registered == null){
            result.rejectValue("email", "message.regError");
        }

        if(result.hasErrors()){
            return new ModelAndView("/user/register",
                    "user", userAccount);
        }else {
            return new ModelAndView("/user/registerSuccess",
                    "user", userAccount);
        }

    }

    private User createUserAccount(UserDto accountDto){
        User registered = null;
        try{
            registered = service.registerNewUser(accountDto);
        }catch (EmailExistsException e){
            return null;
        }catch (NicknameExistsException e){
            return null;
        }

        return registered;
    }

    @GetMapping("/profile")
    public String getProfile(Model model, Principal principal){

        UserDto dto = userMapper.userToUserDto(
                service.findByUsername(
                        principal.getName()
                )
        );

        model.addAttribute("user", dto);

        return "user/profile";
    }

    @GetMapping("/editprofile")
    public String getEditProfile(Model model, Principal principal){

        UserDto dto = userMapper.userToUserDto(service.findByUsername(principal.getName()));
        model.addAttribute("user", dto);

        return "user/editprofile";

    }

    @PostMapping("/updateUserInfo")
    public String updateUserInfo(
        @ModelAttribute("user") UserDto userDto,
        BindingResult bindingResult){

        if(!bindingResult.hasErrors()){
            service.updateUserInfo(userDto);
        }

        return "redirect:/editprofile";
    }

    @GetMapping("/info/{email:.+}")
    public String showUserInfo(@PathVariable String email, Model model){

        UserDto userDto = userMapper.userToUserDto(
                service.findByUsername(email)
        );

        model.addAttribute("user", userDto);

        return "user/info";

    }

    @GetMapping("/find-user")
    public String findUser(@RequestParam("nickname") String nickname, Model model){

        model.addAttribute("user", service.getUserByNickname(nickname));

        return "user/info";

    }

}
















































