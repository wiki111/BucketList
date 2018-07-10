package com.rawik.bucketlist.demo.bootstrap;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.model.BucketList;
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
        userDto.setNickname("wiki");
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
            listDto.setOpen("true");
            listDto.setIsPrivate("false");
            listDto.getItems().add(testItem);
            listDto.setTags("taggy,little taggy");

            BucketListDto listDto2 = new BucketListDto();
            listDto2.setUserId(user.getUserId());
            listDto2.setName("List 2");
            listDto2.setId(2L);
            listDto2.setTags("anothertaggy, taggy");
            listDto2.setIsPrivate("true");

            BucketListDto listDto3 = new BucketListDto();
            listDto3.setUserId(user.getUserId());
            listDto3.setName("List 3");
            listDto3.setId(3L);
            listDto3.setTags("theonlyone");
            listDto3.setIsPrivate("false");

            listService.saveList(listDto);
            listService.saveList(listDto2);
            listService.saveList(listDto3);

            listDto.setName("Updated");
            listService.updateList(listDto);

        }catch (EmailExistsException e){

        }catch (NicknameExistsException e){

        }


    }
}
