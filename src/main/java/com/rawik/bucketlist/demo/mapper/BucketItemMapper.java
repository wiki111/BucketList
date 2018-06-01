package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import org.springframework.stereotype.Component;

@Component
public class BucketItemMapper {

    public BucketItemDto bucketItemToDto(BucketItem item){
        BucketItemDto dto = new BucketItemDto();

        dto.setId(item.getId());

        if(item.getBucketlist() != null){
            dto.setListId(item.getBucketlist().getId());
        }

        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setAddedDate(item.getAddedDate());

        return dto;

    }

    public BucketItem dtoToBucketItem(BucketItemDto dto){
        BucketItem item = new BucketItem();

        item.setId(dto.getId());

        if(dto.getListId() != null){
            BucketList list = new BucketList();
            list.setId(dto.getListId());
            item.setBucketlist(list);
            list.addItem(item);
        }

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImage(dto.getImage());
        item.setAddedDate(dto.getAddedDate());

        return item;
    }

}
