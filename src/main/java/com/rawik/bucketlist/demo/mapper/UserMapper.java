package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    BucketListMapper bucketListMapper;
    PasswordEncoder passwordEncoder;

    public UserMapper(BucketListMapper bucketListMapper, PasswordEncoder passwordEncoder) {
        this.bucketListMapper = bucketListMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto userToUserDto(User user){
        UserDto userDto = new UserDto();

        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPassword(user.getPassword());
        userDto.setMatchingPassword(user.getPassword());
        userDto.setEmail(user.getEmail());

        if(user.getBucketLists() != null && user.getBucketLists().size() > 0){
            user.getBucketLists()
                    .forEach(list ->
                            userDto.getBucketlists().add(
                                    bucketListMapper.bucketListToDto(list)
                            )
                    );
        }

        if(user.getNickname() != null){
            userDto.setNickname(user.getNickname());
        }

        if(user.getPhone() != null){
            userDto.setPhone(user.getPhone());
        }

        if(user.getAddress() != null){
            userDto.setAddress(user.getAddress());
        }

        if(user.getFacebookLink() != null){
            userDto.setFacebookLink(user.getFacebookLink());
        }

        if(user.getGoogleLink() != null){
            userDto.setGoogleLink(user.getGoogleLink());
        }

        if(user.getTwitterLink() != null){
            userDto.setTwitterLink(user.getTwitterLink());
        }

        if(user.getBio() != null){
            userDto.setBio(user.getBio());
        }

        if(user.getInterests() != null){
            userDto.setInterests(user.getInterests());
        }

        return userDto;

    }

    public User userDtoToUser(UserDto userDto){

        User user = new User();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole("ROLE_USER");

        if(userDto.getBucketlists() != null && userDto.getBucketlists().size() > 0){
            userDto.getBucketlists().forEach(bucketlist -> user.getBucketLists().add(
                    bucketListMapper.dtoToBucketList(bucketlist)
            ));
        }

        if(userDto.getNickname() != null){
            user.setNickname(userDto.getNickname());
        }

        if(userDto.getPhone() != null){
            user.setPhone(userDto.getPhone());
        }

        if(userDto.getAddress() != null){
            user.setAddress(userDto.getAddress());
        }

        if(userDto.getFacebookLink() != null){
            user.setFacebookLink(userDto.getFacebookLink());
        }

        if(userDto.getGoogleLink() != null){
            user.setGoogleLink(userDto.getGoogleLink());
        }

        if(userDto.getTwitterLink() != null){
            user.setTwitterLink(userDto.getTwitterLink());
        }

        if(userDto.getBio() != null){
            user.setBio(userDto.getBio());
        }

        if(userDto.getInterests() != null){
            user.setInterests(userDto.getInterests());
        }

        return user;
    }

}
