package com.rawik.bucketlist.demo.repository;

import com.rawik.bucketlist.demo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
