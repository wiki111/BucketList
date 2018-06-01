package com.rawik.bucketlist.demo.dto;

import com.rawik.bucketlist.demo.validation.PasswordMatches;
import com.rawik.bucketlist.demo.validation.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@PasswordMatches
public class UserDto {

    @NotEmpty
    @NotNull
    private String firstName;


    @NotEmpty
    @NotNull
    private String lastName;


    @NotEmpty
    @NotNull
    private String password;
    private String matchingPassword;


    @NotEmpty
    @NotNull
    @ValidEmail
    private String email;

    private Set<BucketListDto> bucketlists;

}
