package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class BucketItemMapperTest {

    @Test
    public void bucketItemToDto() {

        BucketItemMapper mapper = new BucketItemMapper();

        //given

        BucketList bucketList = new BucketList();
        bucketList.setId(1L);

        BucketItem bucketItem = new BucketItem();
        bucketItem.setId(1L);
        bucketItem.setName("TestItem");
        bucketItem.setDescription("Some desc.");
        bucketItem.setBucketlist(bucketList);

        bucketList.addItem(bucketItem);

        //when
        BucketItemDto dto = mapper.bucketItemToDto(bucketItem);

        assertThat(dto.getId(), equalTo(bucketItem.getId()));
        assertThat(dto.getName(), equalTo(bucketItem.getName()));
        assertThat(dto.getDescription(), equalTo(bucketItem.getDescription()));
        assertThat(dto.getListId(), equalTo(bucketItem.getBucketlist().getId()));

    }

    @Test
    public void dtoToBucketItem() {

        BucketItemMapper mapper = new BucketItemMapper();

        BucketItemDto dto = new BucketItemDto();
        dto.setId(1L);
        dto.setName("Test");
        dto.setListId(1L);

        BucketItem item = mapper.dtoToBucketItem(dto);

        assertThat(item.getId(), equalTo(dto.getId()));
        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getBucketlist().getId(), equalTo(dto.getListId()));

    }
}