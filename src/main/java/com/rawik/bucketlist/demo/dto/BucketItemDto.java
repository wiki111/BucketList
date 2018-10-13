package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class BucketItemDto {

    private Long id;
    private Long listId;
    @NotEmpty
    private String name;
    private String description;
    private Long price;
    private String image;
    private Date addedDate;
    private String markedByUsers;
    private Long numberOfMarks;

    public String getImage(){
        return this.image;
    }

}
