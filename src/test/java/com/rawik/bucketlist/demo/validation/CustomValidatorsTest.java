package com.rawik.bucketlist.demo.validation;

import com.rawik.bucketlist.demo.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.*;

import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomValidatorsTest {

    @Autowired
    private Validator validator;

    private UserDto user;

    @Before
    public void setUp(){
        user = new UserDto();
        user.setNickname("wiki");
        user.setPassword("password");
        user.setMatchingPassword("password");
        user.setEmail("matt@daniels.com");
    }

    @Test
    public void testEmailValidator() throws Exception{
        String correctEmail = "matt@gmail.com";

        user.setEmail(correctEmail);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void testEmailValidatorInvalidEmail() throws Exception{
        String incorrectEmail = "com.gmail@matt@fa";

        user.setEmail(incorrectEmail);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size());

    }

    @Test
    public void testPasswordMatching() throws Exception{
        user.setPassword("password");
        user.setMatchingPassword("notmatching");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

}
