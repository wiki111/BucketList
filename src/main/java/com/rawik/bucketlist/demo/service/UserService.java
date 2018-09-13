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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements IUserService {

    UserRepository repository;
    UserMapper userMapper;
    BucketListMapper bucketListMapper;

    public UserService(UserRepository repository, UserMapper userMapper, BucketListMapper bucketListMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
        this.bucketListMapper = bucketListMapper;
    }

    @Transactional
    @Override
    public User registerNewUser(UserDto userDto) throws EmailExistsException, NicknameExistsException{
        if(emailExists(userDto.getEmail())){
            throw new EmailExistsException("There is an account with that email address : " + userDto.getEmail());
        }

        if(nicknameExists(userDto.getNickname())){
            throw new NicknameExistsException("There is an account with that nickname - please choose another.");
        }

        User user = userMapper.userDtoToUser(userDto);

        return repository.save(user);
    }

    @Transactional
    @Override
    public User updateUserInfo(UserDto userDto) {
        User user = repository.findByEmail(userDto.getEmail()).get();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setNickname(userDto.getNickname());
        user.setFacebookLink(userDto.getFacebookLink());
        user.setGoogleLink(userDto.getGoogleLink());
        user.setTwitterLink(userDto.getTwitterLink());
        user.setBio(userDto.getBio());
        user.setInterests(userDto.getInterests());

        repository.save(user);

        return user;
    }

    @Override
    public void updateBucketLists(BucketList bucketList) {
        User savedUser = repository.findById(bucketList.getUser().getUserId()).get();
        Optional<BucketList> usersBucketlist = savedUser.getBucketLists()
                .stream()
                .filter(list -> list.getId() == bucketList.getId())
                .findFirst();

        if(usersBucketlist.isPresent()){
            savedUser.getBucketLists().remove(usersBucketlist.get());
            savedUser.getBucketLists().add(bucketList);
        }else {
            savedUser.getBucketLists().add(bucketList);
        }

        repository.save(savedUser);
    }

    private boolean emailExists(String email){
        Optional<User> user = repository.findByEmail(email);
        if(user.isPresent()){
            return true;
        }
        return false;
    }

    private boolean nicknameExists(String nickname){
        Optional<User> user = repository.findByNickname(nickname);
        if(user.isPresent()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByEmail(username).get();
    }

    @Override
    public UserDto findByUserId(Long id) {

        Optional<User> userOpt = repository.findById(id);

        if(userOpt.isPresent()){
            return userMapper.userToUserDto(userOpt.get());
        }

        return null;
    }

    @Override
    public BucketListDto getUsersListById(Long listId, String username) {

        Optional<User> userOpt = repository.findByEmail(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            Optional<BucketList> foundListOpt = user.getBucketLists().stream().filter(list -> list.getId().equals(listId)).findFirst();
            if(foundListOpt.isPresent()){
                BucketList foundList = foundListOpt.get();
                BucketListDto listDto = bucketListMapper.bucketListToDto(foundList);
                return listDto;
            }
        }

        return null;
    }

    @Override
    public Set<BucketListDto> getUserLists(String email) {

        Optional<User> user = repository.findByEmail(email);

        if(user.isPresent()){
            UserDto userDto = userMapper.userToUserDto(user.get());
            return userDto.getBucketlists();
        }

        return null;
    }

    @Override
    public UserDto getUserByNickname(String nickname) {
        Optional<User> userOptional = repository.findByNickname(nickname);
        if(userOptional.isPresent()){
            return userMapper.userToUserDto(userOptional.get());
        }
        
        return null;
    }

    @Override
    public void updateAvatar(String avatarFilename, String username) {

        Optional<User> user = repository.findByEmail(username);

        if(user.isPresent()){
            User userObj = user.get();
            userObj.setAvatarPath(avatarFilename);
            repository.save(userObj);
        }

    }

    @Override
    public String getAvatarFilename(String username) {

        Optional<User> userOpt = repository.findByEmail(username);

        if(userOpt.isPresent()){
            User user = userOpt.get();
            return user.getAvatarPath();
        }

        return "";
    }


}
