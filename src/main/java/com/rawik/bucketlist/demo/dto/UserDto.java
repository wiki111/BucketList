package com.rawik.bucketlist.demo.dto;

import com.rawik.bucketlist.demo.model.Message;
import com.rawik.bucketlist.demo.validation.PasswordMatches;
import com.rawik.bucketlist.demo.validation.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@PasswordMatches
public class UserDto {

    @NotEmpty
    @NotNull
    private String nickname;

    @NotEmpty
    @NotNull
    private String password;
    private String matchingPassword;


    @NotEmpty
    @NotNull
    @ValidEmail
    private String email;

    private Set<BucketListDto> bucketlists = new HashSet<>();

    private Set<MessageDto> messagesSent = new HashSet<>();
    private Set<MessageDto> messagesReceived = new HashSet<>();

    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    private String facebookLink;
    private String googleLink;
    private String twitterLink;

    private String bio;
    private String interests;

}
