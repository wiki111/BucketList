package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import java.util.*;


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
    private List<String> tags = new ArrayList<>();

}
