package com.rawik.bucketlist.demo.bootstrap;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.MessageService;
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
    private final MessageService messageService;

    public Bootstrap(UserRepository userRepository, UserService userService, BucketListRepository listRepository, BucketListService listService, MessageService messageService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.listRepository = listRepository;
        this.listService = listService;
        this.messageService = messageService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(userRepository.count() == 0L){
            loadUsers();
        }

        setUpMessages();
    }

    private void loadUsers(){
        UserDto userDto = new UserDto();
        userDto.setEmail("matt@daniels.com");
        userDto.setPassword("reter");
        userDto.setMatchingPassword("reter");
        userDto.setNickname("wiki");
        userDto.setAvatarPath("sample_avatar.png");

        UserDto userDto2 = new UserDto();
        userDto2.setEmail("matt3@daniels.com");
        userDto2.setPassword("ravix");
        userDto2.setMatchingPassword("ravix");
        userDto2.setNickname("ravi");
        User user;

        try{
            user = userService.registerNewUser(userDto);
            userService.registerNewUser(userDto2);
            setUpLists(user);
        }catch (EmailExistsException e){

        }catch (NicknameExistsException e){

        }
    }

    private void setUpMessages() {
        messageService.sendMessage("matt@daniels.com", "ravi", "test message");
        messageService.sendMessage("matt@daniels.com", "ravi", "test message 2");
        messageService.sendMessage("matt@daniels.com", "ravi", "test message 3");
        messageService.sendMessage("matt3@daniels.com", "wiki", "another test message");
        messageService.sendMessage("matt3@daniels.com", "wiki", "another test message 2");
        messageService.sendMessage("matt3@daniels.com", "wiki", "another test message 3");
    }

    private void setUpLists(User user) {
        BucketItemDto testItem = new BucketItemDto();
        testItem.setId(1L);
        testItem.setListId(1L);
        testItem.setName("TestItem");
        testItem.setAddedDate(new Date());
        testItem.setPrice(10L);
        testItem.setImage("sample_blitem_img.jpg");
        testItem.setDescription("Curae, Duis sodales wisi placerat faucibus. Sed in neque quis erat. Vivamus volutpat aliquam pharetra ante euismod eget, sagittis ac, molestie a, bibendum porttitor. Aenean vel nibh. Morbi eleifend. Nulla quis quam molestie placerat, nulla nec nunc posuere ante pellentesque auctor eu, dapibus diam. Aliquam luctus pellentesque.");

        BucketItemDto testItem2= new BucketItemDto();
        testItem2.setId(2L);
        testItem2.setListId(1L);
        testItem2.setName("TestItem2");
        testItem2.setAddedDate(new Date());
        testItem2.setPrice(10L);
        testItem2.setImage("sample_blitem_img.jpg");
        testItem2.setDescription("Curae, Duis sodales wisi placerat faucibus. Sed in neque quis erat. Vivamus volutpat aliquam pharetra ante euismod eget, sagittis ac, molestie a, bibendum porttitor. Aenean vel nibh. Morbi eleifend. Nulla quis quam molestie placerat, nulla nec nunc posuere ante pellentesque auctor eu, dapibus diam. Aliquam luctus pellentesque.");

        BucketItemDto testItem3 = new BucketItemDto();
        testItem3.setId(3L);
        testItem3.setListId(1L);
        testItem3.setName("TestItem3");
        testItem3.setAddedDate(new Date());
        testItem3.setPrice(10L);
        testItem3.setImage("sample_blitem_img.jpg");
        testItem3.setDescription("Curae, Duis sodales wisi placerat faucibus. Sed in neque quis erat. Vivamus volutpat aliquam pharetra ante euismod eget, sagittis ac, molestie a, bibendum porttitor. Aenean vel nibh. Morbi eleifend. Nulla quis quam molestie placerat, nulla nec nunc posuere ante pellentesque auctor eu, dapibus diam. Aliquam luctus pellentesque.");

        BucketItemDto testItem4 = new BucketItemDto();
        testItem4.setId(4L);
        testItem4.setListId(1L);
        testItem4.setName("TestItem4");
        testItem4.setAddedDate(new Date());
        testItem4.setPrice(10L);
        testItem4.setImage("sample_blitem_img.jpg");
        testItem4.setDescription("Curae, Duis sodales wisi placerat faucibus. Sed in neque quis erat. Vivamus volutpat aliquam pharetra ante euismod eget, sagittis ac, molestie a, bibendum porttitor. Aenean vel nibh. Morbi eleifend. Nulla quis quam molestie placerat, nulla nec nunc posuere ante pellentesque auctor eu, dapibus diam. Aliquam luctus pellentesque.");

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
        listDto.setCreationDate(new Date());
        listDto.setPhotoPath("sample_bl_img.png");
        listDto.setDescription("Curae, Duis sodales wisi placerat faucibus. Sed in neque quis erat. Vivamus volutpat aliquam pharetra ante euismod eget, sagittis ac, molestie a, bibendum porttitor. Aenean vel nibh. Morbi eleifend. Nulla quis quam molestie placerat, nulla nec nunc posuere ante pellentesque auctor eu, dapibus diam. Aliquam luctus pellentesque.");

        BucketListDto listDto2 = new BucketListDto();
        listDto2.setUserId(user.getUserId());
        listDto2.setName("List 2");
        listDto2.setId(2L);
        listDto2.setTags("anothertaggy, taggy");
        listDto2.setIsPrivate("true");
        listDto2.setCreationDate(new Date());
        listDto2.setAuthorizedUsers("matt3@daniels.com");

        BucketListDto listDto3 = new BucketListDto();
        listDto3.setUserId(user.getUserId());
        listDto3.setName("List 3");
        listDto3.setId(3L);
        listDto3.setTags("theonlyone");
        listDto3.setIsPrivate("false");
        listDto3.setCreationDate(new Date());

        listService.saveList(listDto);
        listService.saveList(listDto2);
        listService.saveList(listDto3);

        listDto.setName("Updated");
        listService.updateList(listDto);

        listService.addItemToList(testItem2, user.getEmail());
        listService.addItemToList(testItem3, user.getEmail());
        listService.addItemToList(testItem4, user.getEmail());
    }
}
