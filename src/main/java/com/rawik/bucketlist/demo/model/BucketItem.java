package com.rawik.bucketlist.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(exclude = {"bucketList"})
public class BucketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BucketList bucketlist;

    private String name;
    private String description;
    private Long price;
    private String image;
    private Date addedDate;



}
