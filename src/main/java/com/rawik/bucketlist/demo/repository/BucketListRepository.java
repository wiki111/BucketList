package com.rawik.bucketlist.demo.repository;

import com.rawik.bucketlist.demo.model.BucketList;
import org.springframework.data.repository.CrudRepository;

public interface BucketListRepository extends CrudRepository<BucketList, Long> {
}
