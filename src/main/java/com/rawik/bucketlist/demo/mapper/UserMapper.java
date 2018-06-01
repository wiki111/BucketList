package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto userToUserDto(User user){
        UserDto userDto = new UserDto();

        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPassword(user.getPassword());
        userDto.setMatchingPassword(user.getPassword());
        userDto.setEmail(user.getEmail());

        if(user.getBucketLists() != null && user.getBucketLists().size() > 0){

        }

    }

}
