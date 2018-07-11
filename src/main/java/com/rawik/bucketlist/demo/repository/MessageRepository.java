package com.rawik.bucketlist.demo.repository;

import com.rawik.bucketlist.demo.model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, Long> {

    Optional<List<Message>> findAllBySenderEmail(String email);
    Optional<List<Message>> findAllByReceiverEmail(String email);

}
