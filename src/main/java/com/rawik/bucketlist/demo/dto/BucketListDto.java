package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.util.*;


@Data
public class BucketListDto {

    private Long id;
    private Long userId;
    private Set<BucketItemDto> items = new HashSet<>();
    @NotEmpty
    private String name;
    private String description;
    private Date creationDate;
    private String open;
    private String isPrivate;
    private String tags;
    private String authorizedUsers;
    private String photoPath;
    private Boolean othersCanMarkItems;

}
