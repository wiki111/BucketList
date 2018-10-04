package com.rawik.bucketlist.demo.repository;

import com.rawik.bucketlist.demo.model.BucketItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BucketItemRepository extends CrudRepository<BucketItem, Long> {
    List<BucketItem> findItemsByMarkedByUsersIn(String nickname);
}
