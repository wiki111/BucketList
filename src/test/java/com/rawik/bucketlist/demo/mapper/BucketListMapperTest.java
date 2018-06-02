package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class BucketListMapperTest {

    @Test
    public void bucketListToDto() {

        BucketItemMapper itemMapper = new BucketItemMapper();
        BucketListMapper listMapper = new BucketListMapper(itemMapper);

        BucketItem item = new BucketItem();
        item.setId(1L);

        User user = new User();
        user.setUserId(1L);

        BucketList list = new BucketList();
        list.setId(1L);
        list.setName("Test");
        list.setUser(user);
        list.addItem(item);

        BucketListDto dto = listMapper.bucketListToDto(list);

        assertThat(dto.getId(), equalTo(list.getId()));
        assertThat(dto.getName(), equalTo(list.getName()));
        assertThat(dto.getUserId(), equalTo(list.getUser().getUserId()));
        for (BucketItem before : list.getItems()) {

            boolean analogueExists = false;

            for (BucketItemDto after : dto.getItems()){
                if(before.getId() == after.getId()){
                    analogueExists = true;
                }
            }

            assertThat(analogueExists, is(true));
        }
    }

    @Test
    public void dtoToBucketList(){

        BucketItemMapper itemMapper = new BucketItemMapper();
        BucketListMapper mapper = new BucketListMapper(itemMapper);

        BucketItemDto itemDto = new BucketItemDto();
        itemDto.setId(1L);

        BucketListDto dto = new BucketListDto();
        dto.setId(1L);
        dto.setName("Test");
        dto.setUserId(1L);
        dto.getItems().add(itemDto);
        dto.setUserId(1L);

        BucketList list = mapper.dtoToBucketList(dto);

        assertThat(list.getId(), equalTo(dto.getId()));
        assertThat(list.getName(), equalTo(dto.getName()));
        assertThat(list.getUser().getUserId(), equalTo(dto.getUserId()));

        for(BucketItemDto dtoItem : dto.getItems() ){

            boolean analogueExists = false;

            for(BucketItem listItem : list.getItems()){
                if(dtoItem.getId() == listItem.getId()){
                    analogueExists = true;
                }
            }

            assertThat(analogueExists, is(true));
        }
    }
}