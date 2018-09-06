package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.MessageDto;
import com.rawik.bucketlist.demo.mapper.MessageMapper;
import com.rawik.bucketlist.demo.model.Message;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.MessageRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceImplIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageMapper messageMapper;

    MessageService messageService;

    User testReceiver;
    User testSender;

    private static boolean messagesPrepared = false;

    @Before
    public void setUp(){
        setUpReceiver();
        setUpSender();
        setUpMessages();

        messageService = new MessageServiceImpl(
                messageRepository,
                userRepository,
                messageMapper);
    }

    private void setUpReceiver(){
        User user = new User();
        user.setEmail("testReceiver@email.com");
        user.setPassword("testpass");
        user.setNickname("testReceiverNick");
        user.setRole("USER");
        testReceiver = user;
        if(!messagesPrepared){
            userRepository.save(user);
        }
    }

    private void setUpSender(){
        User user = new User();
        user.setEmail("testSender@email.com");
        user.setPassword("testpass");
        user.setNickname("testSenderNick");
        user.setRole("USER");
        testSender = user;
        if(!messagesPrepared){
            userRepository.save(user);
        }
    }

    private void setUpMessages(){
        Message message = new Message();
        message.setSender(testSender);
        message.setReceiver(testReceiver);
        message.setDateSent(new Date());
        message.setMessage("pre sent test message");
        if(!messagesPrepared){
            messageRepository.save(message);
            messagesPrepared = true;
        }
    }

    @Test
    @DirtiesContext
    public void sendMessage() {

        messageService.sendMessage(
                testSender.getEmail(),
                testReceiver.getNickname(),
                "test message");

        Optional<List<Message>> savedMessages =
                messageRepository.findAllByReceiverEmail(
                        testReceiver.getEmail());

        assertTrue(savedMessages.isPresent());
        assertNotNull(savedMessages.get()
                .stream()
                .filter(msg -> msg.getReceiver()
                        .getNickname()
                        .equals(testReceiver.getNickname())
                        &&
                        msg.getMessage().equals("test message"))
                .findFirst());
    }

    @Test
    public void getReceivedMessages() {
        List<MessageDto> messages =
                messageService.getReceivedMessages(
                        testReceiver.getEmail()
                );

        Optional<MessageDto> messageDto =
                 messages.stream()
                .filter(msg -> msg
                        .getMessage()
                        .equals("pre sent test message"))
                .findFirst();

        assertTrue(messageDto.isPresent());
    }

    @Test
    public void getSentMessages() {
        List<MessageDto> messageDtos =
                messageService.getSentMessages(
                        testSender.getEmail()
                );

        Optional<MessageDto> sentMessage =
                messageDtos.stream()
                        .filter(msg -> msg.getMessage()
                                .equals("pre sent" +
                                        " test message"))
                        .findFirst();

        assertTrue(sentMessage.isPresent());
    }
}