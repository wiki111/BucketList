package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.jws.soap.SOAPBinding;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    @Mock
    BucketListMapper bucketListMapper;

    @Mock
    MessageMapper messageMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        userMapper = new UserMapper(bucketListMapper, messageMapper, passwordEncoder);
    }

    @Test
    public void userToUserDto() {

        User user = new User();
        user.setFirstName("Name");
        user.setLastName("Last");
        user.setPassword("pass");
        user.setEmail("example@gmail.com");
        user.setNickname("nick");
        user.setFacebookLink("facebook");
        user.setGoogleLink("google");
        user.setTwitterLink("twitter");
        user.setBio("bio");
        user.setInterests("interests");

        BucketList bucketList = new BucketList();
        bucketList.setId(1L);

        user.getBucketLists().add(bucketList);

        BucketListDto bucketListDto = new BucketListDto();
        bucketListDto.setId(1L);

        when(bucketListMapper.bucketListToDto(any(BucketList.class))).thenReturn(bucketListDto);

        UserDto userDto = userMapper.userToUserDto(user);

        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getNickname(), userDto.getNickname());
        assertEquals(user.getBio(), userDto.getBio());
        assertEquals(user.getInterests(), userDto.getInterests());

        verify(bucketListMapper, times(user.getBucketLists().size())).bucketListToDto(any(BucketList.class));

    }

    @Test
    public void userDtoToUser() {
    }
}