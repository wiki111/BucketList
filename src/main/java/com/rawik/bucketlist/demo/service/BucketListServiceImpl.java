package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BucketListServiceImpl implements BucketListService{

    private BucketListRepository listRepository;
    private UserRepository userRepository;
    private BucketListMapper listMapper;

    public BucketListServiceImpl(BucketListRepository listRepository, UserRepository userRepository, BucketListMapper listMapper) {
        this.listRepository = listRepository;
        this.userRepository = userRepository;
        this.listMapper = listMapper;
    }

    @Override
    public BucketList getListById(Long id) {

        Optional<BucketList> optional = listRepository.findById(id);
        if(!optional.isPresent()){
            //todo better exception handling
            throw new RuntimeException();
        }

        return optional.get();
    }

    @Override
    public BucketList saveList(BucketListDto dto) {

        Optional<User> userOptional = userRepository.findById(dto.getUserId());
        if(!userOptional.isPresent()){
            //todo better error handling
            throw new RuntimeException();
        }

        User user = userOptional.get();

        BucketList list = listMapper.dtoToBucketList(dto);

        user.getBucketLists().add(list);

        userRepository.save(user);

        return list;
    }

    @Override
    public BucketList updateList(BucketListDto dto) {

        Optional<BucketList> listToSaveOpt = listRepository.findById(dto.getId());

        if(listToSaveOpt.isPresent()){

            BucketList listToSave = listToSaveOpt.get();

            listToSave.setName(dto.getName());
            listToSave.setDescription(dto.getDescription());
            listToSave.setOpen(listMapper.stringToBoolean(dto.getOpen()));
            listToSave.setIsPrivate(listMapper.stringToBoolean(dto.getIsPrivate()));
            listToSave.setTags(listMapper.tagStringToList(dto.getTags()));

            listRepository.save(listToSave);

            return listToSave;

        }else{
            //todo handle error
        }

        return null;
    }

    @Override
    public void dropList(Long id, String user) {

        Optional<User> userOptional = userRepository.findByEmail(user);

        if(userOptional.isPresent()){

            User foundUser = userOptional.get();

            Optional<BucketList> bucketListOptional =
                    foundUser.getBucketLists()
                            .stream()
                            .filter(list -> list.getId() == id)
                            .findFirst();

            if(bucketListOptional.isPresent()){
                BucketList bucketListToDelete = bucketListOptional.get();
                bucketListToDelete.setUser(null);
                foundUser.getBucketLists().remove(bucketListOptional.get());
                userRepository.save(foundUser);
            }
        }

    }
}
