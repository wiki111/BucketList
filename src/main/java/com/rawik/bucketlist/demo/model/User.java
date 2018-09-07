package com.rawik.bucketlist.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {"bucketLists", "messagesSent", "messagesReceived"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<BucketList> bucketLists = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<Message> messagesSent = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "receiver", cascade = CascadeType.ALL)
    private Set<Message> messagesReceived = new HashSet<>();

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String avatarPath;

    private String nickname;
    private String phone;
    private String address;

    private String facebookLink;
    private String googleLink;
    private String twitterLink;

    private String bio;
    private String interests;

}
