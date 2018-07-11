package com.rawik.bucketlist.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class MessageDto {

    @NotNull
    @NotEmpty
    private String senderNickname;

    @NotNull
    @NotEmpty
    private String receiverNickname;

    @NotNull
    @NotEmpty
    private String message;

    @NotNull
    private Date dateSent;

}
