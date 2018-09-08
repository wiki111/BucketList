package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BucketListServiceImpl implements BucketListService{

    private BucketListRepository listRepository;
    private UserRepository userRepository;
    private BucketListMapper listMapper;
    private UserService userService;
    private BucketItemMapper itemMapper;

    public BucketListServiceImpl(BucketListRepository listRepository,
                                 UserRepository userRepository,
                                 BucketListMapper listMapper,
                                 UserService userService,
                                 BucketItemMapper itemMapper) {
        this.listRepository = listRepository;
        this.userRepository = userRepository;
        this.listMapper = listMapper;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public BucketListDto getUsersListById(Long id, String username) {
        Optional<BucketList> optional = listRepository.findById(id);
        if(!optional.isPresent()){
            //todo better exception handling
            throw new RuntimeException();
        }else {
            BucketList bucketList = optional.get();
            if(bucketList.getUser().getEmail().equals(username)){
                BucketListDto bucketListDto = listMapper.bucketListToDto(optional.get());
                return bucketListDto;
            }else{
                //todo handle user not authorized to access bucketlist
                return null;
            }

        }
    }

    @Override
    public BucketListDto getListById(Long id) {
        Optional<BucketList> optional = listRepository.findById(id);
        if(!optional.isPresent()){
            //todo better exception handling
            throw new RuntimeException();
        }else {
            BucketListDto bucketListDto = listMapper.bucketListToDto(optional.get());
            return bucketListDto;
        }
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

        User savedUser = userRepository.save(user);

        Optional<BucketList> savedBucketlist = savedUser.getBucketLists()
                .stream().filter(
                        list1 -> list1.getCreationDate().equals(dto.getCreationDate()))
                .findFirst();

        if(savedBucketlist.isPresent()){
            return savedBucketlist.get();
        }else{
            return null;
        }
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
            listToSave.setAuthorizedUsers(listMapper.authorizedStringToList(dto.getAuthorizedUsers()));
            if(dto.getPhotoPath() != null){
                if(!dto.getPhotoPath().isEmpty()){
                    listToSave.setPhotoPath(dto.getPhotoPath());
                }
            }

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
                listRepository.save(bucketListToDelete);
                listRepository.deleteById(bucketListToDelete.getId());
            }
        }

    }

    @Override
    public void dropListItem(Long listid, Long itemid, String user) {

        Optional<User> userOptional = userRepository.findByEmail(user);

        if(userOptional.isPresent()){

            User foundUser = userOptional.get();

            Optional<BucketList> bucketListOptional =
                    foundUser.getBucketLists()
                            .stream()
                            .filter(list -> list.getId() == listid)
                            .findFirst();

            if(bucketListOptional.isPresent()){
                BucketList foundBucketlist = bucketListOptional.get();

                Optional<BucketItem> bucketItemOptional = foundBucketlist.getItems()
                        .stream()
                        .filter(item -> item.getId() == itemid)
                        .findFirst();

                if(bucketItemOptional.isPresent()){
                    BucketItem foundItem = bucketItemOptional.get();
                    foundItem.setBucketlist(null);
                    foundBucketlist.getItems().remove(bucketItemOptional.get());
                    listRepository.save(foundBucketlist);
                }
            }
        }
    }

    @Override
    public List<BucketListDto> getPublicBucketlists() {

        List<BucketList> bucketLists = listRepository.findBucketListByIsPrivateIsFalse();

        List<BucketListDto> bucketListDtos = new ArrayList<>();

        for (BucketList list : bucketLists) {
            bucketListDtos.add(listMapper.bucketListToDto(list));
        }

        return bucketListDtos;
    }

    @Override
    public List<BucketListDto> getPublicBucketlistsByTag(String tags) {

        List<BucketListDto> bucketListDtos = new ArrayList<>();
        List<String> tagList = listMapper.tagStringToList(tags);

        for (BucketList list : listRepository.findBucketListByIsPrivateIsFalseAndTagsIn(tagList)) {
            BucketListDto bucketListDto = listMapper.bucketListToDto(list);
            bucketListDtos.add(bucketListDto);
        }

        return bucketListDtos;
    }

    @Override
    public boolean addItemToList(BucketItemDto itemDto, String username) {

        User user = userService.findByUsername(username);
        BucketList bucketList = searchUsersListsForId(user, itemDto.getListId());

        if(bucketList != null){
            bucketList.getItems().add(itemMapper.dtoToBucketItem(itemDto));
            userService.updateBucketLists(bucketList);
            return true;
        }else{
            //todo handle bucketlist doesn't exist
            return false;
        }
    }

    @Override
    public List<BucketListDto> getBucketlistsAvailableForUser(String username) {

        List<BucketListDto> availableBucketlists = new ArrayList<>();

        for(BucketList list : listRepository.findBucketListsByAuthorizedUsersIn(username)){
            BucketListDto listDto = listMapper.bucketListToDto(list);
            availableBucketlists.add(listDto);
        }

        return availableBucketlists;
    }

    @Override
    public String getImageForListId(Long listid) {

        Optional<BucketList> bucketOpt = listRepository.findById(listid);

        if(bucketOpt.isPresent()){
            BucketList bucketList = bucketOpt.get();
            return bucketList.getPhotoPath();
        }

        return "";
    }


    private BucketList searchUsersListsForId(User user, Long listId){
        Optional<BucketList> bucketListOptional =
                user.getBucketLists()
                        .stream()
                        .filter(list -> list.getId()
                                .equals(listId))
                        .findFirst();

        if(bucketListOptional.isPresent()){
            return bucketListOptional.get();
        }else {
            return null;
        }
    }
}
