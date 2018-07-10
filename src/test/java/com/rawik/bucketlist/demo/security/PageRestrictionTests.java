package com.rawik.bucketlist.demo.security;

import com.rawik.bucketlist.demo.dto.UserDto;
import static  org.hamcrest.CoreMatchers.*;

import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;

import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageRestrictionTests {

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testHomepageIsAccessibleWithoutLogin() throws Exception{
        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("homepage"));
    }

    @Test
    public void testLoginLoads() throws Exception{
        this.mockMvc
                .perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"));
    }

    @Test
    public void testRegisterFormLoads() throws Exception{
        this.mockMvc
                .perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attribute("user", is(new UserDto())));
    }

    @Test
    @DirtiesContext
    public void testPostRegistration() throws Exception{

        UserDto userDto = new UserDto();
        userDto.setNickname("wikita");
        userDto.setPassword("reter");
        userDto.setMatchingPassword("reter");
        userDto.setEmail("matt2@daniels.com");

        this.mockMvc
                .perform(post("/register").flashAttr("user", userDto))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/registerSuccess"));
    }

    @Test
    @DirtiesContext
    public void testRegisterAndLoginUser() throws Exception, EmailExistsException, NicknameExistsException {
        UserDto userDto = new UserDto();
        userDto.setNickname("wikima");
        userDto.setPassword("reter");
        userDto.setMatchingPassword("reter");
        userDto.setEmail("matt2@daniels.com");

        User user = userService.registerNewUser(userDto);

        this.mockMvc.perform(formLogin("/login")
                .user("matt2@daniels.com")
                .password("reter"))
            .andExpect(SecurityMockMvcResultMatchers.authenticated().withRoles("USER"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/successLogin"));
    }

}
