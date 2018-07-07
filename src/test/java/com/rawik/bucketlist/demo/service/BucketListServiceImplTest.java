package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    BucketListMapper mapper;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        bucketListService = new BucketListServiceImpl(listRepository, userRepository, mapper);
    }

    @Test
    public void getListById() {

        BucketList list = new BucketList();
        list.setId(1L);

        when(listRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(list));

        BucketList returnedList = bucketListService.getListById(1L);

        assertNotNull("List is null", returnedList);
        verify(listRepository, times(1)).findById(anyLong());
        assertThat(list.getId(), equalTo(returnedList.getId()));
    }

    @Test
    public void saveList() {

        BucketList list = new BucketList();
        list.setId(1L);

        BucketListDto dto = new BucketListDto();
        dto.setId(1L);
        dto.setUserId(1L);

        User user = new User();
        user.setUserId(1L);
        user.getBucketLists().add(list);


        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(user));
        when(mapper.dtoToBucketList(any(BucketListDto.class))).thenReturn(list);

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

        when(mapper.dtoToBucketList(any(BucketListDto.class))).thenReturn(list);
        when(listRepository.findById(anyLong())).thenReturn(Optional.of(list));
        when(listRepository.save(any(BucketList.class))).thenReturn(list);

        BucketList savedList = bucketListService.updateList(dto);

        assertThat(dto.getId(), equalTo(savedList.getId()));
        verify(listRepository, times(1)).save(any(BucketList.class));

    }

    @Test
    public void dropList() {
        bucketListService.dropList(1L);
        verify(listRepository, times(1)).deleteById(anyLong());
    }
}