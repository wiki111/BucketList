package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BucketListServiceImplIntegrationTest {

    @Autowired
    BucketListRepository bucketListRepository;

    @Autowired
    BucketListMapper bucketListMapper;

    @Autowired
    UserRepository userRepository;

    BucketListService bucketListService;

    @Before
    public void setUp(){
        bucketListService = new BucketListServiceImpl(bucketListRepository, userRepository, bucketListMapper);
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
