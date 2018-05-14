package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserDTO {

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
    private String email;

}
