package com.rawik.bucketlist.demo.bootstrap;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final UserService userService;
    private final BucketListRepository listRepository;
    private final BucketListService listService;

    public Bootstrap(UserRepository userRepository, UserService userService, BucketListRepository listRepository, BucketListService listService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.listRepository = listRepository;
        this.listService = listService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(userRepository.count() == 0L){
            loadUsers();
        }
    }

    private void loadUsers(){
        UserDto userDto = new UserDto();
        userDto.setEmail("matt@daniels.com");
        userDto.setPassword("reter");
        userDto.setMatchingPassword("reter");
        userDto.setFirstName("Matt");
        userDto.setLastName("Daniels");
        User user;
        try{
            user = userService.registerNewUser(userDto);

            BucketItemDto testItem = new BucketItemDto();
            testItem.setId(1L);
            testItem.setListId(1L);
            testItem.setName("TestItem");
            testItem.setAddedDate(new Date());
            testItem.setPrice(10L);
            testItem.setImage("image");

            BucketListDto listDto = new BucketListDto();
            listDto.setUserId(user.getUserId());
            listDto.setName("Test");
            listDto.setId(1L);
            listDto.setCreationDate(new Date());
            listDto.setDescription("Desc");
            listDto.setOpen(true);
            listDto.getItems().add(testItem);

            listService.saveList(listDto);

            listDto.setName("Updated");
            listService.updateList(listDto);

        }catch (EmailExistsException e){

        }


    }
}
