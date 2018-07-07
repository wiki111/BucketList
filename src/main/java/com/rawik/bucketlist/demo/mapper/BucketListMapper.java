package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        dto.setOpen(String.valueOf(list.getOpen()));
        dto.setIsPrivate(String.valueOf(list.getIsPrivate()));
        dto.setTags(tagListToString(list.getTags()));

        if(list.getItems() != null){
            list.getItems().forEach(item -> dto.getItems().add(itemMapper.bucketItemToDto(item)));
        }

        return dto;
    }

    public BucketList dtoToBucketList(BucketListDto dto){
        BucketList list = new BucketList();

        list.setId(dto.getId());
        list.setName(dto.getName());
        list.setCreationDate(dto.getCreationDate());
        list.setDescription(dto.getDescription());
        list.setOpen(stringToBoolean(dto.getOpen()));
        list.setIsPrivate(stringToBoolean(dto.getIsPrivate()));
        list.setTags(tagStringToList(dto.getTags()));

        if(dto.getUserId() != null){
            User user = new User();
            user.setUserId(dto.getUserId());
            list.setUser(user);
        }

        dto.getItems().forEach(item ->
                list.addItem(itemMapper.dtoToBucketItem(item))
        );

        return list;
    }

    public List<String> tagStringToList(String tags){
        ArrayList<String> tagList = new ArrayList<>();

        if(tags != null){
            String[] tagArray = tags.replaceAll("\\s+","").split(",");

            for (String tag : tagArray) {
                tagList.add(tag);
            }
        }

        return tagList;
    }

    public String tagListToString(List<String> tagList){

        StringBuilder tagString =  new StringBuilder();

        for (String tag: tagList) {
            tagString.append(tag + ", ");
        }

        if(tagString.length() > 1){
            tagString.setCharAt(tagString.length() - 1, ' ');
        }

        return tagString.toString();

    }

    public Boolean stringToBoolean(String text){

        if(text != null){
            if(text.toLowerCase().equals("true")){
                return true;
            }
        }

        return false;
    }

}
