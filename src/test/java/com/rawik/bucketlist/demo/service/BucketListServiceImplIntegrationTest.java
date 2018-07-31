package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BucketListServiceImplIntegrationTest {

    @Autowired
    BucketListRepository bucketListRepository;

    @Autowired
    BucketListMapper bucketListMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    BucketItemMapper itemMapper;

    BucketListService bucketListService;

    String testUserNickname = "testNickname";
    String testUserEmail = "test@email.com";
    String testPassword = "testPass";

    String testListName = "testListName";

    Long testListId = 999L;
    Long testUserId;

    @Before
    public void setUp(){
        bucketListService = new BucketListServiceImpl(
                bucketListRepository,
                userRepository,
                bucketListMapper,
                userService,
                itemMapper);

        setUpTestEntities();
    }

    private void setUpTestEntities(){
        setUpTestUsers();
    }

    private void setUpTestUsers(){
        UserDto testUser = new UserDto();
        testUser.setNickname(testUserEmail);
        testUser.setEmail(testUserEmail);
        testUser.setPassword(testPassword);
        testUser.setMatchingPassword(testPassword);

        User savedTestUser;

        try {
            savedTestUser = userService.registerNewUser(testUser);
            testUserId = savedTestUser.getUserId();
            setUpTestBucketlists(savedTestUser);
        }catch (NicknameExistsException e){

        } catch (EmailExistsException e) {
            e.printStackTrace();
        }
    }

    private void setUpTestBucketlists(User user){

        BucketListDto bucketListDto = new BucketListDto();
        bucketListDto.setUserId(user.getUserId());
        bucketListDto.setTags("testTag1, testTag2");
        bucketListDto.setName(testListName);
        bucketListDto.setIsPrivate("false");
        bucketListDto.setCreationDate(new Date());
        bucketListDto.setDescription("testDesc");
        bucketListDto.setId(testListId);

        BucketItemDto testItem = new BucketItemDto();
        testItem.setPrice(10L);
        testItem.setImage("testImage");
        testItem.setDescription("testItemDesc");
        testItem.setName("testItemName");
        testItem.setAddedDate(new Date());
        testItem.setListId(testListId);
        testItem.setId(999L);

        bucketListDto.getItems().add(testItem);

        BucketList savedBucketlist = bucketListService.saveList(bucketListDto);
        testListId = savedBucketlist.getId();
        Optional<BucketList> test = bucketListRepository.findById(999L);
    }

    @Test
    public void getUsersListByIdTest(){
        BucketListDto returnedListDto = bucketListService.getUsersListById(testListId, testUserEmail);

        assertFalse(returnedListDto.getItems().isEmpty());
        assertEquals(returnedListDto.getId(), testListId);
        assertEquals(returnedListDto.getName(), testListName);
        assertEquals(returnedListDto.getUserId(), testUserId);

    }

    @Test
    @DirtiesContext
    public void testUpdateBucketlist(){

        BucketListDto listToSaveDto = new BucketListDto();
        listToSaveDto.setId(1L);
        listToSaveDto.setUserId(1L);

        BucketList savedList = bucketListService.updateList(listToSaveDto);

        assertEquals(savedList.getId(), listToSaveDto.getId());
        assertEquals(savedList.getUser().getUserId(), listToSaveDto.getUserId());
    }

}
