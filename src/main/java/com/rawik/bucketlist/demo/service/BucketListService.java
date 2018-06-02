package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.model.BucketList;

import java.util.Set;

public interface BucketListService {

    BucketList getListById(Long id);
    BucketList saveList(BucketListDto dto);
    BucketList updateList(BucketListDto dto);
    void dropList(Long id);

}
