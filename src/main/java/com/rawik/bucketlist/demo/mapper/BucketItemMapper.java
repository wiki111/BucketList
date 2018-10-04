package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BucketItemMapper {

    public BucketItemDto bucketItemToDto(BucketItem item){
        BucketItemDto dto = new BucketItemDto();

        dto.setId(item.getId());

        if(item.getBucketlist() != null){
            dto.setListId(item.getBucketlist().getId());
        }

        if(item.getMarkedByUsers() != null){
            dto.setMarkedByUsers(markedByUsersToString(item.getMarkedByUsers()));
            dto.setNumberOfMarks(Long.valueOf(item.getMarkedByUsers().size()));
        }

        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setAddedDate(item.getAddedDate());
        dto.setImage(item.getImage());

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

        if(dto.getMarkedByUsers() != null){
            item.setMarkedByUsers(markedByUsersToList(dto.getMarkedByUsers()));
        }

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImage(dto.getImage());
        item.setAddedDate(dto.getAddedDate());

        return item;
    }

    public List<String> markedByUsersToList(String tags){
        ArrayList<String> tagList = new ArrayList<>();

        if(tags != null){
            String[] tagArray = tags.replaceAll("\\s+","").split(",");

            for (String tag : tagArray) {
                tagList.add(tag);
            }
        }

        return tagList;
    }

    public String markedByUsersToString(List<String> tagList){

        StringBuilder tagString =  new StringBuilder();

        for (String tag: tagList) {
            tagString.append(tag + ", ");
        }

        if(tagString.length() > 1){
            tagString.setCharAt(tagString.length() - 1, ' ');
        }

        return tagString.toString();

    }


}
