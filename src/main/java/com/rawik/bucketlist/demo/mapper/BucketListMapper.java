package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BucketListMapper {

    private BucketItemMapper itemMapper;

    public BucketListMapper(BucketItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public BucketListDto bucketListToDto(BucketList list){
        BucketListDto dto = new BucketListDto();

        dto.setId(list.getId());
        dto.setUserId(list.getUser().getUserId());
        dto.setName(list.getName());
        dto.setDescription(list.getDescription());
        dto.setCreationDate(list.getCreationDate());
        dto.setOpen(list.getOpen());

        if(list.getItems() != null){
            list.getItems().forEach(item -> dto.getItems().add(itemMapper.bucketItemToDto(item)));
        }

        return dto;
    }



}
