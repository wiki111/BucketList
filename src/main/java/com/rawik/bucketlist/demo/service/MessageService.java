package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.MessageDto;

import java.util.List;

public interface MessageService {

    void sendMessage(String senderNickname, String receiverNickname, String message);

}
