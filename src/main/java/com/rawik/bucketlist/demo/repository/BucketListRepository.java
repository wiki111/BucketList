package com.rawik.bucketlist.demo.repository;

import com.rawik.bucketlist.demo.model.BucketList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BucketListRepository extends CrudRepository<BucketList, Long> {

    List<BucketList> findBucketListByIsPrivateIsFalseAndTagsIn(List<String> tags);
    List<BucketList> findBucketListByIsPrivateIsFalse();
    List<BucketList> findBucketListsByAuthorizedUsersIn(String username);

}
