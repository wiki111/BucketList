package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Data
public class BucketListDto {

    private Long id;
    private Long userId;
    private Set<BucketItemDto> items = new HashSet<>();
    private String name;
    private String description;
    private Date creationDate;
    private Boolean open;
    private Boolean isPrivate;

}
