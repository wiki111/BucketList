package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.MessageDto;

import java.util.List;

public interface MessageService {

    void sendMessage(String senderEmail, String receiverNickname, String message);
    List<MessageDto> getReceivedMessages(String email);
    List<MessageDto> getSentMessages(String email);

}
