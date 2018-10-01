package com.rawik.bucketlist.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = {"bucketList"})
public class BucketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BucketList bucketlist;

    @ElementCollection
    private List<String> markedByUsers = new ArrayList<>();

    private String name;

    @Lob
    private String description;

    private Long price;
    private String image;
    private Date addedDate;



}
