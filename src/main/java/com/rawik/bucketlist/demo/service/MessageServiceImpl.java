package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.MessageDto;
import com.rawik.bucketlist.demo.mapper.MessageMapper;
import com.rawik.bucketlist.demo.model.Message;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.MessageRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageMapper messageMapper;

    @Override
    public void sendMessage(String senderEmail, String receiverNickname, String message) {

        Message messageObj = new Message();

        Optional<User> sender = userRepository.findByEmail(senderEmail);
        Optional<User> receiver = userRepository.findByNickname(receiverNickname);

        if(sender.isPresent() && receiver.isPresent()){

            User senderObj = sender.get();
            User receiverObj = receiver.get();

            messageObj.setSender(senderObj);
            messageObj.setReceiver(receiverObj);
            messageObj.setDateSent(new Date());
            messageObj.setMessage(message);

            receiverObj.getMessagesReceived().add(messageObj);
            senderObj.getMessagesSent().add(messageObj);

            messageRepository.save(messageObj);

        }
    }

    @Override
    public List<MessageDto> getReceivedMessages(String email) {

        List<MessageDto> messages = new ArrayList<>();

        Optional<List<Message>> savedMessagesOptional = messageRepository.findAllByReceiverEmail(email);
        if(savedMessagesOptional.isPresent()){
            for (Message message : savedMessagesOptional.get()) {
                messages.add(messageMapper.messageToDto(message));
            }

            return messages;
        }

        return null;
    }

    @Override
    public List<MessageDto> getSentMessages(String email) {

        List<MessageDto> messages = new ArrayList<>();

        Optional<List<Message>> savedMessagesOptional = messageRepository.findAllBySenderEmail(email);
        if(savedMessagesOptional.isPresent()){

            for (Message message : savedMessagesOptional.get()) {
                messages.add(messageMapper.messageToDto(message));
            }

            return messages;
        }

        return null;
    }
}
