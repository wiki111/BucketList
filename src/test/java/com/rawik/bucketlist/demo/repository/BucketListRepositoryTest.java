package com.rawik.bucketlist.demo.repository;

import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BucketListRepositoryTest {

    @Autowired
    BucketListRepository bucketListRepository;

    @Test
    public void findBucketListByTagsIn() {

        ArrayList<String> tags = new ArrayList<>();
        tags.add("taggy");


        List<BucketList> bucketLists = bucketListRepository.findBucketListByIsPrivateIsFalseAndTagsIn(tags);

        assertEquals(bucketLists.size(), 1);

    }
}