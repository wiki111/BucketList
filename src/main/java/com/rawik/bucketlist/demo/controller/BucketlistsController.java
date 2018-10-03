package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.StorageException;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.StorageService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.ws.WebEndpoint;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class BucketlistsController {

    private UserService userService;
    private BucketListService bucketListService;
    private StorageService storageService;

    private static final String MANAGE_LIST_LINK = "/user/bucketlist/manage/";
    private static final String SHOW_USERS_BUCKETLISTS = "bucketlist/show-users-bucketlists";
    private static final String SHOW_BUCKETLISTS = "bucketlist/show-bucketlists";

    public BucketlistsController(UserService userService,
                                 BucketListService bucketListService,
                                 StorageService storageService) {
        this.userService = userService;
        this.bucketListService = bucketListService;
        this.storageService = storageService;
    }

    @GetMapping("/user/bucketlists")
    public String showUsersBucketlists(Model model, Principal principal){
        model.addAttribute(
                "lists",
                userService.getUserLists(principal.getName()));
        return SHOW_BUCKETLISTS;
    }

    @GetMapping("/bucketlists")
    public String showPublicBucketlists(Model model){
        model.addAttribute(
                "lists",
                bucketListService.getPublicBucketlists());
        return SHOW_BUCKETLISTS;
    }

    @GetMapping("/bucketlists/addnew")
    public String getAddNewBucketlistForm(Model model, Principal principal){
        model.addAttribute("username", principal.getName());
        model.addAttribute("list", new BucketListDto());
        model.addAttribute("titletext", "Add new list");
        return "bucketlist/add-list";
    }

    @PostMapping("/bucketlists/addnew")
    public String addNewBucketlist(@ModelAttribute("list") BucketListDto listDto,
                                   @RequestParam("imagefile") MultipartFile imagefile,
                                   Principal principal){

        if(!imagefile.isEmpty()){
            String imageFilename = storageService.store(imagefile, principal.getName());
            listDto.setPhotoPath(imageFilename);
        }

        User user = userService.findByUsername(principal.getName());
        listDto.setUserId(user.getUserId());
        listDto.setCreationDate(new Date());

        bucketListService.saveList(listDto);

        return "redirect:/user/bucketlists";
    }

    @GetMapping(MANAGE_LIST_LINK + "{id}")
    public String showUsersBucketlistForManagement(
            @ModelAttribute("id") Long id, Model model, Principal principal){

        BucketListDto listDto = userService
                .getUsersListById(id, principal.getName());
        model.addAttribute("list", listDto);
        model.addAttribute("username", principal.getName());

        if(bucketListService.getUsersListById(id, principal.getName()) == null ){
            model.addAttribute("ownedByAuth", false);
        }else{
            model.addAttribute("ownedByAuth", true);
        }

        return "bucketlist/show-list-details";
    }

    @GetMapping("/bucketlist/details/{id}")
    public String showPublicBucketlistDetails(
            @PathVariable("id") Long id, Model model, Principal principal){

        BucketListDto bucketListDto = bucketListService.getListById(id);
        model.addAttribute("list", bucketListDto);

        UserDto user = userService.findByUserId(bucketListDto.getUserId());
        model.addAttribute("username", user.getNickname());

        if(bucketListService.getUsersListById(id, principal.getName()) == null ){
            model.addAttribute("ownedByAuth", false);
        }else{
            model.addAttribute("ownedByAuth", true);
        }

        return "bucketlist/show-list-details";
    }

    @GetMapping("/addItemToList/{id}")
    public String getAddListItemPage(@PathVariable("id") long id, Principal principal, Model model){
        BucketListDto list = userService.getUsersListById(id, principal.getName());
        model.addAttribute("list",list);
        model.addAttribute("username", principal.getName());
        return "bucketlist/add-item";
    }

    @PostMapping("/addListItem")
    public String addListItem(WebRequest request, Principal principal, @RequestParam("image") MultipartFile multipartFile){

        try{
            String imagePath = storageService.store(multipartFile, principal.getName());
            bucketListService.addItemToList(
                    setUpBucketItemDto(request, imagePath), principal.getName());
        }catch (Exception e){
            bucketListService.addItemToList(
                    setUpBucketItemDto(request, ""), principal.getName());
        }



        return "redirect:" + MANAGE_LIST_LINK + request.getParameter("listID");
    }

    @GetMapping("/editListItem/{listId}/{itemId}")
    public String editListItem(@PathVariable("listId") long listId, @PathVariable("itemId") long itemId, Model model, Principal principal){

        model.addAttribute("edit", true);
        model.addAttribute("username", principal.getName());

        BucketListDto listDto = bucketListService.getListById(listId);
        model.addAttribute("list", listDto);

        Optional<BucketItemDto> itemDtoOpt = listDto.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst();
        if(itemDtoOpt.isPresent()){
            model.addAttribute("item", itemDtoOpt.get());
        }

        return "bucketlist/add-item";
    }

    private BucketItemDto setUpBucketItemDto(WebRequest request, String imagePath){

        BucketItemDto itemDto = new BucketItemDto();
        itemDto.setListId(Long.valueOf(request.getParameter("listID")));

        if(request.getParameter("itemId") != null){
            itemDto.setId(Long.valueOf(request.getParameter("itemId")));
        }

        itemDto.setName(request.getParameter("name"));
        itemDto.setDescription(request.getParameter("description"));

        try{
            itemDto.setPrice(Long.valueOf(request.getParameter("price")));
        }catch (NumberFormatException e){
            itemDto.setPrice(0L);
        }

        itemDto.setAddedDate(new Date());

        if(request.getParameter("photoPath") != null && imagePath.isEmpty()){
            itemDto.setImage(request.getParameter("photoPath"));
        }else{
            itemDto.setImage(imagePath);
        }

        return itemDto;
    }

    @GetMapping( MANAGE_LIST_LINK + "{listid}/edit" )
    public String editBucketlist(
            @PathVariable Long listid, Model model, Principal principal){

        BucketListDto bucketListDto = bucketListService
                .getUsersListById(listid, principal.getName());

        if(bucketListDto == null){
            return "errors/not-authorized-error";
        }else{
            model.addAttribute("list", bucketListDto);
            model.addAttribute("titletext", "EDIT BUCKETLIST");
            return "bucketlist/edit";
        }

    }


    @PostMapping( MANAGE_LIST_LINK + "{listid}/edit")
    public String editBucketlist(
            @ModelAttribute("list") BucketListDto bucketListDto,
            Principal principal, @RequestParam("imagefile") MultipartFile multipartFile){

        if(!multipartFile.isEmpty()){
            String imageFilename = storageService.store(multipartFile, principal.getName());
            bucketListDto.setPhotoPath(imageFilename);
        }

        bucketListService.updateList(bucketListDto);

        return "redirect:" + MANAGE_LIST_LINK + bucketListDto.getId();
    }

    @GetMapping("/bucketlist/delete/{id}")
    public String deleteBucketlist(@PathVariable Long id, Principal principal){
        bucketListService.dropList(id, principal.getName());
        return "redirect:/bucketlists";
    }

    @GetMapping("/user/bucketlist/{listid}/item/{itemid}/delete")
    public String deleteBucketlistItem(
            @PathVariable Long listid, @PathVariable Long itemid,
            Principal principal){

        bucketListService.dropListItem(listid, itemid, principal.getName());

        return "redirect:" + MANAGE_LIST_LINK + listid;
    }

    @PostMapping("/search")
    public String showPublicBucketlistsByTags(
            @RequestParam("query") String query, Model model){

        model.addAttribute("lists",
                bucketListService.getPublicBucketlistsByTag(query));

        return SHOW_BUCKETLISTS;
    }


    @GetMapping("/showsharedlists")
    public String showSharedListsForUser(Model model, Principal principal){

        model.addAttribute("lists", bucketListService.getBucketlistsAvailableForUser(principal.getName()));

        return SHOW_BUCKETLISTS;

    }

    //todo refactor two methods to be one ?
    // todo add default images 270x170px

    @RequestMapping(value = "/getBucketlistImage/{nickname}/{photoPath:.+}")
    @ResponseBody
    public byte[] getBucketlistImage(@PathVariable String photoPath, @PathVariable String nickname){

        String filename = photoPath;

        Path imagePath;

        if(!nickname.contains("@")){
            UserDto user = userService.getUserByNickname(nickname);
            imagePath = storageService.load(filename, user.getEmail());
        }else {
            imagePath = storageService.load(filename, nickname);
        }

        try{
            byte[] imageData = Files.readAllBytes(imagePath);
            return imageData;
        }catch (IOException e){
            throw new StorageException("Trouble serving content..." + e);
        }
    }

    @RequestMapping(value = "/getItemImage/{nickname}/{image:.+}")
    @ResponseBody
    public byte[] getItemImage(@PathVariable String nickname, @PathVariable String image){

        String filename = image;
        Path imagePath;

        if(!nickname.contains("@")){
            UserDto user = userService.getUserByNickname(nickname);
            imagePath = storageService.load(filename, user.getEmail());
        }else {
            imagePath = storageService.load(filename, nickname);
        }

        try{
            byte[] imageData = Files.readAllBytes(imagePath);
            return imageData;
        }catch (IOException e){
            throw new StorageException("Trouble serving content..." + e);
        }
    }


}
