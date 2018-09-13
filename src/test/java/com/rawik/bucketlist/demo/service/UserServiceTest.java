package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.mapper.UserMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.jws.soap.SOAPBinding;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    UserMapper userMapper;

    @Mock
    BucketListMapper bucketListMapper;

    UserService userService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        userService = new UserService(repository, userMapper, bucketListMapper);
    }

    @Test
    public void registerNewUser() {

        UserDto userDto = new UserDto();
        User user = new User();

        when(userMapper.userDtoToUser(any(UserDto.class))).thenReturn(user);
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenReturn(user);

        try {
            User returnedUser = userService.registerNewUser(userDto);
            assertEquals(returnedUser, user);
        } catch (EmailExistsException e) {
            e.printStackTrace();
        }catch (NicknameExistsException e){

        }

    }

    @Test
    public void updateUserInfo() {
        String email = "some@email.com";
        User user = new User();
        user.setEmail(email);
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName("first");
        userDto.setLastName("last");
        userDto.setNickname("nick");
        userDto.setFacebookLink("facebook");
        userDto.setGoogleLink("google");
        userDto.setTwitterLink("twitter");
        userDto.setBio("bio");
        userDto.setInterests("interests");

        Optional<User> userOptional = Optional.of(user);

        when(repository.findByEmail(anyString())).thenReturn(userOptional);
        when(repository.save(any(User.class))).thenReturn(user);

        User returnedUser = userService.updateUserInfo(userDto);

        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getBio(), userDto.getBio());
        assertEquals(user.getInterests(), userDto.getInterests());
        assertEquals(user.getNickname(), userDto.getNickname());
    }


    @Test
    @Ignore
    public void updateBucketLists() {

        BucketList bucketList = new BucketList();
        bucketList.setId(1L);

        User user = new User();
        user.setEmail("anyemail");
        user.getBucketLists().add(bucketList);
        User savedUser = new User();
        user.getBucketLists().add(bucketList);

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.updateBucketLists(bucketList);

        verify(repository, times(1)).save(savedUser);
    }

    @Test
    public void findByUsername() {
        User user = new User();
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(user));
        userService.findByUsername("someemail");
        verify(repository, times(1)).findByEmail("someemail");
    }


    @Test
    public void getUsersListById() {

        Long listId = 1L;
        String username = "someemail";
        User user =  new User();
        BucketList bucketList = new BucketList();
        bucketList.setId(1L);
        user.getBucketLists().add(bucketList);
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(user));
        BucketListDto bucketListDto = new BucketListDto();
        bucketListDto.setId(1L);
        when(bucketListMapper.bucketListToDto(any(BucketList.class))).thenReturn(bucketListDto);

        BucketListDto returnedListDto = userService.getUsersListById(listId, username);

        verify(repository, times(1)).findByEmail(username);
        assertEquals(returnedListDto, bucketListDto);
    }

    @Test
    public void getUserLists() {

        String email = "email";

        User user = new User();
        UserDto userDto = new UserDto();
        BucketListDto bucketListDto = new BucketListDto();
        bucketListDto.setId(1L);
        userDto.getBucketlists().add(bucketListDto);

        when(userMapper.userToUserDto(any(User.class))).thenReturn(userDto);
        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        Set<BucketListDto> returnedListSetDto = userService.getUserLists(email);

        assertEquals(returnedListSetDto, userDto.getBucketlists());
    }
}