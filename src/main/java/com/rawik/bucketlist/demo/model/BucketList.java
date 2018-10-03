package com.rawik.bucketlist.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.engine.profile.Fetch;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@EqualsAndHashCode(exclude = {"items", "user"})
@ToString(exclude = {"items", "user"})
public class BucketList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "bucketlist")
    private Set<BucketItem> items = new HashSet<>();

    @ManyToOne
    private User user;

    private String name;

    @Lob
    private String description;

    private Date creationDate;
    private Boolean open;
    private Boolean isPrivate;
    private Boolean othersCanMarkItems;
    private String photoPath;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> authorizedUsers = new ArrayList<>();

    public void addItem(BucketItem item){
        item.setBucketlist(this);
        this.items.add(item);

    }

}
