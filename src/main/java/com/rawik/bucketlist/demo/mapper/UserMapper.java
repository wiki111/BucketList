package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    BucketListMapper bucketListMapper;
    MessageMapper messageMapper;
    PasswordEncoder passwordEncoder;

    public UserMapper(BucketListMapper bucketListMapper, MessageMapper messageMapper,PasswordEncoder passwordEncoder) {
        this.bucketListMapper = bucketListMapper;
        this.messageMapper = messageMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto userToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPassword(user.getPassword());
        userDto.setMatchingPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setAvatarPath(user.getAvatarPath());
        if(user.getBucketLists() != null && user.getBucketLists().size() > 0){
            user.getBucketLists()
                    .forEach(list ->
                            userDto.getBucketlists().add(
                                    bucketListMapper.bucketListToDto(list)
                            )
                    );
        }
        if(user.getMessagesSent() != null & user.getMessagesSent().size() > 0){
            user.getMessagesSent()
                    .forEach(message -> userDto.getMessagesSent().add(
                            messageMapper.messageToDto(message)
                    ));
        }
        if(user.getMessagesReceived() != null & user.getMessagesReceived().size() > 0) {
            user.getMessagesReceived()
                    .forEach(message -> userDto.getMessagesReceived().add(
                            messageMapper.messageToDto(message)
                    ));

        }
        if(user.getNickname() != null){
            userDto.setNickname(user.getNickname());
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
        user.setAvatarPath(userDto.getAvatarPath());
        if(userDto.getBucketlists() != null && userDto.getBucketlists().size() > 0){
            userDto.getBucketlists().forEach(bucketlist -> user.getBucketLists().add(
                    bucketListMapper.dtoToBucketList(bucketlist)
            ));
        }
        if(userDto.getMessagesSent() != null && userDto.getMessagesSent().size() > 0){
            userDto.getMessagesSent()
                    .forEach(messageDto ->  user.getMessagesSent().add(
                            messageMapper.dtoToMessage(messageDto)
                    ));
        }
        if(userDto.getMessagesReceived() != null && userDto.getMessagesReceived().size() > 0){
            userDto.getMessagesReceived()
                    .forEach(messageDto ->  user.getMessagesReceived().add(
                            messageMapper.dtoToMessage(messageDto)
                    ));
        }
        if(userDto.getNickname() != null){
            user.setNickname(userDto.getNickname());
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
