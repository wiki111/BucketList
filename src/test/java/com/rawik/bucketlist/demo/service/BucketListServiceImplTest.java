package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketItemRepository;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BucketListServiceImplTest {

    BucketListService bucketListService;

    @Mock
    UserRepository userRepository;

    @Mock
    BucketListRepository listRepository;

    @Mock
    BucketListMapper listMapper;

    @Mock
    UserService userService;

    @Mock
    BucketItemMapper itemMapper;

    @Mock
    BucketItemRepository itemRepository;

    @Mock
    StorageService storageService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        bucketListService = new BucketListServiceImpl(
                listRepository,
                userRepository,
                listMapper,
                userService,
                itemMapper,
                itemRepository,
                storageService);
    }

    @Test
    public void getUsersListByIdTest(){

        Long id = 1L;
        String username = "username";

        User user = new User();
        user.setEmail(username);

        BucketList bucketList = new BucketList();
        bucketList.setId(1L);
        bucketList.setUser(user);

        Optional<BucketList> optional = Optional.of(bucketList);

        BucketListDto dto = new BucketListDto();
        dto.setId(id);
        dto.setUserId(id);

        when(listRepository.findById(id)).thenReturn(optional);
        when(listMapper.bucketListToDto(any(BucketList.class))).thenReturn(dto);

        BucketListDto returnedDto = bucketListService.getUsersListById(id, username);

        assertEquals(returnedDto.getId(), id);
        verify(listRepository, times(1)).findById(id);
        verify(listMapper, times(1)).bucketListToDto(any(BucketList.class));

    }

    @Test
    public void getListById() {

        BucketList list = new BucketList();
        list.setId(1L);

        BucketListDto bucketListDto = new BucketListDto();
        bucketListDto.setId(1L);

        when(listRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(list));
        when(listMapper.bucketListToDto(any(BucketList.class))).thenReturn(bucketListDto);

        BucketListDto returnedList = bucketListService.getListById(1L);

        assertNotNull("List is null", returnedList);
        verify(listRepository, times(1)).findById(anyLong());
        assertThat(list.getId(), equalTo(returnedList.getId()));
    }

    @Test
    public void saveList() {

        BucketList list = new BucketList();
        list.setId(1L);
        list.setCreationDate(new Date());

        BucketListDto dto = new BucketListDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setCreationDate(list.getCreationDate());

        User user = new User();
        user.setUserId(1L);
        user.getBucketLists().add(list);


        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(user));
        when(listMapper.dtoToBucketList(any(BucketListDto.class))).thenReturn(list);
        when(userRepository.save(any(User.class))).thenReturn(user);

        BucketList savedList = bucketListService.saveList(dto);

        assertThat(list.getId(), equalTo(savedList.getId()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateList() {

        BucketListDto dto = new BucketListDto();
        dto.setId(1L);

        BucketList list = new BucketList();
        list.setId(1L);

        when(listMapper.dtoToBucketList(any(BucketListDto.class))).thenReturn(list);
        when(listRepository.findById(anyLong())).thenReturn(Optional.of(list));
        when(listRepository.save(any(BucketList.class))).thenReturn(list);

        BucketList savedList = bucketListService.updateList(dto);

        assertThat(dto.getId(), equalTo(savedList.getId()));
        verify(listRepository, times(1)).save(any(BucketList.class));

    }

}