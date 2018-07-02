package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.model.User;

public interface IUserService {
    User registerNewUser(UserDto userDto) throws EmailExistsException;
    User updateUserInfo(UserDto userDto);
    User updateBucketLists(User user);
    User findByUsername(String username);
    User findById(Long id);
}
