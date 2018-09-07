package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.mapper.UserMapper;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.StorageService;
import com.rawik.bucketlist.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.soap.SOAPBinding;
import java.security.Principal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    UserService userService;

    @Mock
    UserMapper userMapper;

    @Mock
    BindingResult bindingResult;

    @Mock
    StorageService storageService;

    @Mock
    Model model;

    @Mock
    Principal principal;

    UserController userController;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        userController = new UserController(userService, userMapper, storageService);
    }

    @Test
    public void getLogin() {
        String view = userController.getLogin();
        assertEquals("user/login", view);
    }

    @Test
    public void getRegisterForm() {
        ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);
        String view = userController.getRegisterForm(model);
        verify(model, times(1)).addAttribute(eq("user"), userDtoCaptor.capture());
        assertNotNull(userDtoCaptor.getValue());
    }

    @Test
    public void registerNewUser() {
        UserDto userAcconunt = new UserDto();

        User user = new User();
        try {
            when(userService.registerNewUser(any(UserDto.class))).thenReturn(user);
        } catch (EmailExistsException e) {
            e.printStackTrace();
        }catch (NicknameExistsException e){

        }

        when(bindingResult.hasErrors()).thenReturn(false);

        ModelAndView modelAndView = userController.registerNewUser(userAcconunt, bindingResult);

        assertEquals("/user/registerSuccess", modelAndView.getViewName());
        assertEquals(userAcconunt, modelAndView.getModel().get("user"));
    }

    @Test
    public void getProfile() {
        UserDto userDto = new UserDto();
        User user = new User();
        when(userMapper.userToUserDto(any(User.class))).thenReturn(userDto);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(principal.getName()).thenReturn("Name");

        String view = userController.getProfile(model, principal);

        ArgumentCaptor<UserDto> argumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        verify(model, times(1)).addAttribute(eq("user"), argumentCaptor.capture());

        assertEquals("user/profile",view);
        assertEquals(userDto, argumentCaptor.getValue());
    }

    @Test
    public void updateUserInfo() {
        UserDto userDto = new UserDto();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = userController.updateUserInfo(userDto, bindingResult);

        ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);

        verify(userService, times(1)).updateUserInfo(userDtoCaptor.capture());

        assertEquals(userDto, userDtoCaptor.getValue());
    }

    @Test
    public void showUserInfo() {

        String email = "example@gmail.com";

        UserDto userDto = new UserDto();
        User user = new User();

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(userMapper.userToUserDto(any(User.class))).thenReturn(userDto);

        ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);

        String view = userController.showUserInfo(email, model);

        verify(model, times(1)).addAttribute(eq("user"), userDtoCaptor.capture());

        assertEquals(userDto, userDtoCaptor.getValue());
        assertEquals("user/info", view);

    }
}