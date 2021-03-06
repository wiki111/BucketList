package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.exceptions.StorageException;
import com.rawik.bucketlist.demo.mapper.UserMapper;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.StorageService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;


@Controller
public class UserController {

    private UserService service;
    private UserMapper userMapper;
    private StorageService storageService;

    public UserController(UserService service, UserMapper userMapper, StorageService storageService) {
        this.service = service;
        this.userMapper = userMapper;
        this.storageService = storageService;
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
    public ModelAndView registerNewUser(@ModelAttribute("user") @Valid UserDto userAccount, BindingResult result){

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
        UserDto dto = service.getUserByUsername(principal.getName());
        model.addAttribute("user", dto);
        model.addAttribute("editable", true);
        model.addAttribute("username", principal.getName());

        return "user/profile";
    }

    @GetMapping("/user/{username}")
    public String getUsersInfo(@PathVariable String username, Model model, Principal principal){

        UserDto userDto = service.getUserByNickname(username);

        if(userDto.getEmail().equals(principal.getName())){
            return "redirect:/profile";
        }

        model.addAttribute("user", userDto);
        model.addAttribute("editable", false);
        model.addAttribute("username", userDto.getNickname());

        return "user/profile";
    }

    @GetMapping("/editprofile")
    public String getEditProfile(Model model, Principal principal){
        UserDto dto = userMapper.userToUserDto(service.findByUsername(principal.getName()));
        model.addAttribute("user", dto);
        return "user/editprofile";
    }

    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@ModelAttribute("user") UserDto userDto, BindingResult bindingResult){
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

    @PostMapping("/updateAvatar")
    public String handleAvatarUpload(@RequestParam("avatarFile") MultipartFile avatarFile, Principal principal){
        String avatarFilename = storageService.store(avatarFile, principal.getName());
        service.updateAvatar(avatarFilename, principal.getName());
        return "redirect:" + "/profile";
    }

    @RequestMapping(value = "/getAvatar/{username:.+}")
    @ResponseBody
    public byte[] getAvatar(@PathVariable String username, HttpServletRequest request){
        String avatarFilename = service.getAvatarFilename(username);
        Path path;

        if(avatarFilename==null || avatarFilename.isEmpty()){
            path = storageService.loadDefaultAvatar();
        }else{
            if(!username.contains("@")){
                UserDto userDto = service.getUserByNickname(username);
                username = userDto.getEmail();
            }
            path = storageService.load(avatarFilename, username);
        }

        try{
            byte[] avatarData = Files.readAllBytes(path);
            return avatarData;
        }catch (IOException e){
            throw new StorageException("Trouble serving content..." + e);
        }
    }

}
















































