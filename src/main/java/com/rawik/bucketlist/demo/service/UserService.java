package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.mapper.UserMapper;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService implements IUserService {


    @Autowired
    UserRepository repository;

    @Autowired
    UserMapper userMapper;

    @Transactional
    @Override
    public User registerNewUser(UserDto userDto) throws EmailExistsException{
        if(emailExists(userDto.getEmail())){
            throw new EmailExistsException("There is an account with that email address : " + userDto.getEmail());
        }

        User user = userMapper.userDtoToUser(userDto);

        return repository.save(user);
    }

    @Transactional
    @Override
    public User updateUserInfo(UserDto userDto) {
        User user = repository.findByEmail(userDto.getEmail());

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setNickname(userDto.getNickname());
        user.setAddress(userDto.getAddress());
        user.setPhone(userDto.getPhone());
        user.setFacebookLink(userDto.getFacebookLink());
        user.setGoogleLink(userDto.getGoogleLink());
        user.setTwitterLink(userDto.getTwitterLink());
        user.setBio(userDto.getBio());
        user.setInterests(userDto.getInterests());

        repository.save(user);
        return user;
    }

    private boolean emailExists(String email){
        User user = repository.findByEmail(email);
        if(user != null){
            return true;
        }
        return false;
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByEmail(username);
    }

    @Override
    public User findById(Long id) {
        return null;
    }
}
