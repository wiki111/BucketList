package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.model.BucketList;

import java.util.List;
import java.util.Set;

public interface BucketListService {

    BucketListDto getUsersListById(Long id, String username);
    BucketListDto getListById(Long id);
    BucketList saveList(BucketListDto dto);
    BucketList updateList(BucketListDto dto);
    void dropList(Long id, String user);
    void dropListItem(Long listid, Long itemid, String user);
    List<BucketListDto> getPublicBucketlists();
    List<BucketListDto> getPublicBucketlistsByTag(String tags);
    List<BucketListDto> getPublicBucketlistsByUsername(String username);
    boolean addItemToList(BucketItemDto itemDto, String username);
    List<BucketListDto> getBucketlistsAvailableForUser(String username);
    String getImageForListId(Long listid);
    List<Long> getIdsOfItemsMarkedByUser(String username);
    boolean markItem(Long itemId, String causerUsername);
    boolean unmarkItem(Long itemId, String causerUsername);
    String getCurrentItemImageName(Long itemId);
    String getCurrentListImageName(Long listId);
}
