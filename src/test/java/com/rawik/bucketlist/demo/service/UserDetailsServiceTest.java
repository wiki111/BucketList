package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserDetailsServiceTest {

    @Mock
    UserRepository repository;

    UserDetailsService userDetailsService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.userDetailsService = new UserDetailsService(repository);
    }

    @Test
    public void loadUserByUsername() {

        String email = "some@email.com";

        com.rawik.bucketlist.demo.model.User user = new com.rawik.bucketlist.demo.model.User();
        user.setEmail(email);
        user.setPassword("pass");
        user.setRole("ROLE_USER");

        when(repository.findByEmail(anyString())).thenReturn(java.util.Optional.ofNullable(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(userDetails.getUsername(), user.getEmail());
        assertEquals(userDetails.getPassword(), user.getPassword());
    }
}