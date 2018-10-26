package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.exceptions.NotAuthorizedException;
import com.rawik.bucketlist.demo.exceptions.NotFoundException;
import com.rawik.bucketlist.demo.exceptions.OperationException;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.BucketItemRepository;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.*;

@Service
public class BucketListServiceImpl implements BucketListService{

    private BucketListRepository listRepository;
    private UserRepository userRepository;
    private BucketListMapper listMapper;
    private UserService userService;
    private BucketItemMapper itemMapper;
    private BucketItemRepository itemRepository;
    private StorageService storageService;

    public BucketListServiceImpl(BucketListRepository listRepository,
                                 UserRepository userRepository,
                                 BucketListMapper listMapper,
                                 UserService userService,
                                 BucketItemMapper itemMapper,
                                 BucketItemRepository itemRepository,
                                 StorageService storageService) {
        this.listRepository = listRepository;
        this.userRepository = userRepository;
        this.listMapper = listMapper;
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.storageService = storageService;
    }

    @Override
    public BucketListDto getUsersListById(Long id, String username) {
        Optional<BucketList> optional = listRepository.findById(id);
        if(!optional.isPresent()){
            throw new NotFoundException("List doesn't exist in database.");
        }else {
            BucketList bucketList = optional.get();
            /*if(bucketList.getUser().getEmail().equals(username)){
                BucketListDto bucketListDto = listMapper.bucketListToDto(optional.get());
                return bucketListDto;
            }else{
                throw new NotAuthorizedException("User not authorized to access this information.");
            }*/
            BucketListDto bucketListDto = listMapper.bucketListToDto(optional.get());
            return bucketListDto;
        }
    }

    @Override
    public BucketListDto getListById(Long id) {
        Optional<BucketList> optional = listRepository.findById(id);
        if(!optional.isPresent()){
            throw new NotFoundException("List doesn't exist in database.");
        }else {
            BucketListDto bucketListDto = listMapper.bucketListToDto(optional.get());
            return bucketListDto;
        }
    }

    @Override
    public BucketList saveList(BucketListDto dto) {
        Optional<User> userOptional = userRepository.findById(dto.getUserId());
        if(!userOptional.isPresent()){
            throw new NotFoundException("Invalid user - information doesn't exist in database.");
        }
        User user = userOptional.get();
        BucketList list = listMapper.dtoToBucketList(dto);
        user.getBucketLists().add(list);
        User savedUser = userRepository.save(user);
        Optional<BucketList> savedBucketlist = savedUser.getBucketLists()
                .stream().filter(list1 -> list1.getCreationDate().equals(dto.getCreationDate()))
                .findFirst();
        if(savedBucketlist.isPresent()){
            return savedBucketlist.get();
        }else{
            throw new OperationException("Encountered a problem while saving list. Please try again or contact " +
                    "customer service.");
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
            listToSave.getTags().clear();
            listToSave.setTags(listMapper.tagStringToList(dto.getTags()));
            listToSave.getAuthorizedUsers().clear();
            listToSave.setAuthorizedUsers(listMapper.authorizedStringToList(dto.getAuthorizedUsers()));
            listToSave.setOthersCanMarkItems(dto.getOthersCanMarkItems());
            if(dto.getPhotoPath() != null){
                if(!dto.getPhotoPath().isEmpty()){
                    listToSave.setPhotoPath(dto.getPhotoPath());
                }
            }
            listRepository.save(listToSave);
            return listToSave;
        }else{
            throw new NotFoundException("Cannot update list, because it doesn't exist in database.");
        }
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
                storageService.deleteFile(foundUser.getEmail(), bucketListToDelete.getPhotoPath());
                deleteAllItemImages(bucketListToDelete, foundUser.getEmail());
                foundUser.getBucketLists().remove(bucketListOptional.get());
                userRepository.save(foundUser);
                listRepository.save(bucketListToDelete);
                listRepository.deleteById(bucketListToDelete.getId());
            }else{
                throw new NotFoundException("Cannot find relevant information in database. Please try again");
            }
        }else{
            throw new NotAuthorizedException("Missing authorization to process this request. Please try again.");
        }
    }

    private void deleteAllItemImages(BucketList bucketList, String ownerUsername){
        for(BucketItem item : bucketList.getItems()){
            storageService.deleteFile(ownerUsername, item.getImage());
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
                    storageService.deleteFile(foundUser.getEmail(), foundItem.getImage());
                    foundItem.setBucketlist(null);
                    foundBucketlist.getItems().remove(bucketItemOptional.get());
                    listRepository.save(foundBucketlist);
                }else{
                    throw new NotFoundException("Cannot find relevant information in database.");
                }
            }else {
                throw new NotFoundException("Cannot find relevant information in database.");
            }
        }else{
            throw new NotAuthorizedException("Lacking authorization to perform this request. Please try again ?");
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
    public List<BucketListDto> getPublicBucketlistsByUsername(String username) {
        Optional<User> userOpt = userRepository.findByEmail(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            List<BucketListDto> bucketListDtos = new ArrayList<>();
            for(BucketList list : user.getBucketLists()){
                if(!list.getIsPrivate()){
                    BucketListDto listDto = listMapper.bucketListToDto(list);
                    bucketListDtos.add(listDto);
                }
            }
            return bucketListDtos;
        }
        return null;
    }

    @Override
    public boolean addItemToList(BucketItemDto itemDto, String username) {
        User user = userService.findByUsername(username);
        BucketList bucketList = searchUsersListsForId(user, itemDto.getListId());
        if(bucketList != null){
            Optional<BucketItem> itemOpt = bucketList.getItems().stream()
                    .filter(item -> item.getId().equals(itemDto.getId())).findFirst();
            if(itemOpt.isPresent()){
                bucketList.getItems().remove(itemOpt.get());
            }
            bucketList.getItems().add(itemMapper.dtoToBucketItem(itemDto));
            userService.updateBucketLists(bucketList);
            return true;
        }else{
            throw new NotFoundException("Cannot find relevant data.");
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

    @Override
    public List<Long> getIdsOfItemsMarkedByUser(String username) {
        List<Long> ids = new ArrayList<>();
        Optional<User> userOpt = userRepository.findByEmail(username);
        String nickname = "";
        if(userOpt.isPresent()){
            nickname = userOpt.get().getNickname();
        }
        List<BucketItem> items = itemRepository.findItemsByMarkedByUsersIn(nickname);
        for(BucketItem item : items){
            ids.add(item.getId());
        }
        return ids;
    }

    @Override
    public boolean markItem(Long itemId, String causerUsername) {
        Optional<BucketItem> itemOpt = itemRepository.findById(itemId);
        if(itemOpt.isPresent()){
            BucketItem item = itemOpt.get();
            Optional<User> userOpt = userRepository.findByEmail(causerUsername);
            if(userOpt.isPresent()){
                if(!userOpt.get().getEmail().equals(item.getBucketlist().getUser().getEmail()) && !item.getBucketlist().getOpen()){
                    return false;
                }
                if(!item.getMarkedByUsers().contains(userOpt.get().getNickname())){
                    item.getMarkedByUsers().add(userOpt.get().getNickname());
                    itemRepository.save(item);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean unmarkItem(Long itemId, String causerUsername) {
        Optional<BucketItem> itemOpt = itemRepository.findById(itemId);
        if(itemOpt.isPresent()){
            BucketItem item = itemOpt.get();
            Optional<User> userOpt = userRepository.findByEmail(causerUsername);
            if(userOpt.isPresent()){
                if(item.getMarkedByUsers().remove(userOpt.get().getNickname())){
                    itemRepository.save(item);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getCurrentItemImageName(Long itemId) {
        String imageName = "";
        Optional<BucketItem> itemOpt = itemRepository.findById(itemId);
        if(itemOpt.isPresent()){
            BucketItem item = itemOpt.get();
            imageName = item.getImage();
        }
        return imageName;
    }

    @Override
    public String getCurrentListImageName(Long listId) {
        String imageName = "";
        Optional<BucketList> listOpt = listRepository.findById(listId);
        if(listOpt.isPresent()){
            BucketList list = listOpt.get();
            imageName = list.getPhotoPath();
        }
        return imageName;
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
            throw new NotFoundException("Cannot find relevant data.");
        }
    }
}
