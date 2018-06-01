package com.rawik.bucketlist.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "user")
    private Set<BucketList> bucketLists = new HashSet<>();

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;

}
