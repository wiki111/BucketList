package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BucketItemDto {

    private Long id;
    private Long listId;
    private String name;
    private String description;
    private Long price;
    private String image;
    private Date addedDate;

}
