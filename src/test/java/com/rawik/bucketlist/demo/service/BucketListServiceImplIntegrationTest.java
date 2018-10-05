package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.EmailExistsException;
import com.rawik.bucketlist.demo.exceptions.NicknameExistsException;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketItemRepository;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
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

    @Autowired
    BucketItemRepository itemRepository;

    @Autowired
    StorageService storageService;

    BucketListService bucketListService;

    String testUserEmail = "newtestemail@email.com";
    String testPassword = "testPass";

    String testListName = "testListName";

    Long testListId;
    Long testListItemId;

    BucketListDto testListDto;
    BucketItemDto testListItemDto;

    @Before
    public void setUp(){
            bucketListService = new BucketListServiceImpl(
                    bucketListRepository,
                    userRepository,
                    bucketListMapper,
                    userService,
                    itemMapper,
                    itemRepository,
                    storageService);

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
        }catch (NicknameExistsException e){

        } catch (EmailExistsException e) {
            e.printStackTrace();
        }

        savedTestUser = userService.findByUsername(testUserEmail);
        setUpTestBucketlists(savedTestUser);
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
        testListItemDto = testItem;

        bucketListDto.getItems().add(testItem);
        testListDto = bucketListDto;

        BucketList savedBucketlist = bucketListService.saveList(bucketListDto);
        testListId = savedBucketlist.getId();
        testListItemId = savedBucketlist.getItems().stream().findFirst().get().getId();
    }

    @Test
    public void getUsersListByIdTest(){
        BucketListDto returnedListDto = bucketListService.getUsersListById(testListId, testUserEmail);

        assertFalse(returnedListDto.getItems().isEmpty());
        assertEquals(returnedListDto.getId(), testListId);
        assertEquals(returnedListDto.getName(), testListName);

    }

    @Test
    public void getListByIdTest(){

        BucketListDto listDto = bucketListService.getListById(testListId);

        assertEquals(testListName, listDto.getName());
        assertEquals(testListId, listDto.getId());

    }

    @Test
    @DirtiesContext
    public void saveListTest(){
        BucketList listObj = bucketListService.saveList(testListDto);

        assertEquals(listObj.getName(), testListDto.getName());
        assertFalse(listObj.getTags().isEmpty());
        assertFalse(listObj.getItems().isEmpty());

        Optional<BucketList> bucketListOpt = bucketListRepository.findById(listObj.getId());
        assertTrue(bucketListOpt.isPresent());

        Optional<User> userOpt = userRepository.findById(bucketListOpt.get().getUser().getUserId());
        assertTrue(userOpt.isPresent());
        assertTrue(userOpt.get().getBucketLists()
                .stream()
                .filter(list -> list.getId()
                        .equals(bucketListOpt.get().getId()))
                .findFirst() != null);
    }

    @Test
    @DirtiesContext
    public void dropListTest(){
        bucketListService.dropList(testListId, testUserEmail);

        Optional<User> userOpt = userRepository.findByEmail(testUserEmail);

        Optional<BucketList> bucketListOptional = bucketListRepository.findById(testListId);
        assertFalse(bucketListOptional.isPresent());
    }

    @Test
    public void dropListItemTest(){
        bucketListService.dropListItem(testListId, testListItemId, testUserEmail);
        Optional<BucketList> bucketlistOpt = bucketListRepository.findById(testListId);
        assertTrue(bucketlistOpt.isPresent());
        BucketList bucketList = bucketlistOpt.get();
        assertTrue(bucketList.getItems().isEmpty());
    }

    @Test
    public void getPublicBucketlistsTest() {
        List<BucketListDto> bucketlists = bucketListService.getPublicBucketlists();
        for (BucketListDto listDto : bucketlists) {
            assertTrue(listDto.getIsPrivate().equals("false"));
        }
    }

    @Test
    public void getPublicBucketlistsByTagTest(){
        List<BucketListDto> listDtos = bucketListService.getPublicBucketlistsByTag("testTag1");
        for (BucketListDto list : listDtos) {
            assertTrue(list.getTags().contains("testTag1"));
            assertTrue(list.getIsPrivate().equals("false"));
        }
    }

    @Test
    public void addItemToListTest() {
        testListItemDto.setListId(testListId);
        testListItemDto.setName("addItemTestItem");
        boolean isSuccessful = bucketListService.addItemToList(testListItemDto, testUserEmail);
        assertTrue(isSuccessful);
        Optional<BucketList> bucketlistOpt = bucketListRepository.findById(testListId);
        assertTrue(bucketlistOpt.isPresent());
        assertTrue(
                bucketlistOpt.get().getItems()
                        .stream()
                        .filter(list -> list.getName().equals("addItemTestItem"))
                        .findFirst() != null);

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
