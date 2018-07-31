package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.MessageDto;
import com.rawik.bucketlist.demo.model.Message;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MessageMapperTest {

    Message message;
    MessageDto messageDto;
    User user1, user2;

    String messageText = "message text";

    @InjectMocks
    MessageMapper messageMapper;

    @Mock
    UserRepository userRepository;

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);

        user1 = new User();
        user1.setUserId(1L);
        user1.setNickname("Ravi");

        user2 = new User();
        user2.setUserId(2L);
        user2.setNickname("Rete");

        message = new Message();
        message.setMessageId(1L);
        message.setSender(user1);
        message.setReceiver(user2);
        message.setMessage(messageText);
        message.setDateSent(new Date());

        messageDto = new MessageDto();
        messageDto.setSenderNickname(user1.getNickname());
        messageDto.setReceiverNickname(user2.getNickname());
        messageDto.setMessage(messageText);
        messageDto.setDateSent(new Date());

        messageMapper = new MessageMapper(userRepository);
    }

    @Test
    public void messageToDto() {
        MessageDto returnedMessageDto = messageMapper.messageToDto(message);

        assertEquals(user1.getNickname(), returnedMessageDto.getSenderNickname());
        assertEquals(user2.getNickname(), returnedMessageDto.getReceiverNickname());
        assertNotNull(returnedMessageDto.getDateSent());
        assertEquals(returnedMessageDto.getMessage(), messageText);
    }

    @Test
    public void dtoToMessage() {

        when(userRepository.findByNickname(user1.getNickname())).thenReturn(Optional.of(user1));
        when(userRepository.findByNickname(user2.getNickname())).thenReturn(Optional.of(user2));

        Message returnedMessage = messageMapper.dtoToMessage(messageDto);

        assertEquals(returnedMessage.getSender().getUserId(), user1.getUserId());
        assertEquals(returnedMessage.getReceiver().getUserId(), user2.getUserId());
        assertEquals(returnedMessage.getMessage(), messageText);
        assertNotNull(returnedMessage.getDateSent());

    }
}