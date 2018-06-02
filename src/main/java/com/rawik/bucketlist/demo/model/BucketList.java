package com.rawik.bucketlist.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {"items", "user"})
public class BucketList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "bucketlist")
    private Set<BucketItem> items = new HashSet<>();

    @ManyToOne
    private User user;

    private String name;
    private String description;
    private Date creationDate;
    private Boolean open;

    public void addItem(BucketItem item){
        item.setBucketlist(this);
        this.items.add(item);

    }

}
