package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.model.User;

import java.util.Set;

public interface IUserService {
    User registerNewUser(UserDto userDto) throws EmailExistsException, NicknameExistsException;
    User updateUserInfo(UserDto userDto);
    User updateBucketLists(User user);
    User findByUsername(String username);
    UserDto findByUserId(Long id);
    BucketListDto getUsersListById(Long listId, String us);
    Set<BucketListDto> getUserLists(String email);
}
