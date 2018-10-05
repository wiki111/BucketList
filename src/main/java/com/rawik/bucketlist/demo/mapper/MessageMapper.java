package com.rawik.bucketlist.demo.mapper;

import com.rawik.bucketlist.demo.dto.MessageDto;
import com.rawik.bucketlist.demo.model.Message;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MessageMapper {

    private UserRepository userRepository;

    public MessageMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MessageDto messageToDto(Message message){
        MessageDto messageDto = new MessageDto();
        messageDto.setSenderNickname(message.getSender().getNickname());
        messageDto.setReceiverNickname(message.getReceiver().getNickname());
        messageDto.setDateSent(message.getDateSent());
        messageDto.setMessage(message.getMessage());
        return messageDto;
    }

    public Message dtoToMessage(MessageDto messageDto){
        Message message = new Message();
        Optional<User> sender = userRepository.findByNickname(messageDto.getSenderNickname());
        Optional<User> receiver = userRepository.findByNickname(messageDto.getReceiverNickname());
        if(sender.isPresent() && receiver.isPresent()){
            message.setSender(sender.get());
            message.setReceiver(receiver.get());
        }
        message.setMessage(messageDto.getMessage());
        message.setDateSent(messageDto.getDateSent());
        return message;
    }

}
