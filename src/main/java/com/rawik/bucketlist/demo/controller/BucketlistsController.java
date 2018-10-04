package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.StorageService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class BucketlistsController {

    private UserService userService;
    private BucketListService bucketListService;
    private StorageService storageService;

    private static final String MANAGE_LIST_LINK = "/user/bucketlist/manage/";
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
        model.addAttribute("lists", userService.getUserLists(principal.getName()));
        model.addAttribute("byTags", "true");
        model.addAttribute("title", "Your bucketlists");
        return SHOW_BUCKETLISTS;
    }

    @GetMapping("/bucketlists")
    public String showPublicBucketlists(Model model){
        model.addAttribute("lists", bucketListService.getPublicBucketlists());
        model.addAttribute("byTags", "true");
        model.addAttribute("title", "Public bucketlists");
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
    public String addNewBucketlist(@ModelAttribute("list") BucketListDto listDto, @RequestParam("imagefile") MultipartFile imagefile,
                                   Principal principal){
        String imagePath = saveImage(imagefile, principal.getName());
        bucketListService.saveList(setUpBucketlistDto(listDto, imagePath, principal.getName()));
        return "redirect:/user/bucketlists";
    }

    private BucketListDto setUpBucketlistDto(BucketListDto listDto, String imagePath, String username){
        User user = userService.findByUsername(username);
        listDto.setUserId(user.getUserId());
        listDto.setCreationDate(new Date());
        if(imagePath != null) listDto.setPhotoPath(imagePath);
        return listDto;
    }

    private String saveImage(MultipartFile imageFile, String username){
        if(!imageFile.isEmpty()){
            String imageFilename = storageService.store(imageFile, username);
            return imageFilename;
        }else{
            return null;
        }
    }

    @GetMapping("/bucketlist/details/{id}")
    public String showBucketlistDetails(@PathVariable("id") Long id, Model model, Principal principal){
        model = prepareModelForShowBucketlistsPage(model, principal, id);
        return "bucketlist/show-list-details";
    }

    private Model prepareModelForShowBucketlistsPage(Model model, Principal principal, Long id){

        BucketListDto bucketListDto = bucketListService.getListById(id);
        UserDto user = userService.findByUserId(bucketListDto.getUserId());

        model.addAttribute("list", bucketListDto);
        model.addAttribute("username", user.getNickname());

        if(principal != null){
            model.addAttribute("ownedByAuth" ,checkIfUserOwnsList(id, principal.getName()));
            model.addAttribute("userLoggedIn", true);
            model.addAttribute("idsOfMarkedItems", bucketListService.idsOfItemsMarkedByUser(principal.getName()));
        }else {
            model.addAttribute("ownedByAuth", false);
            model.addAttribute("userLoggedIn", false);
            model.addAttribute("idsOfMarkedItems", new ArrayList<>());
        }

        return model;
    }

    private boolean checkIfUserOwnsList(Long listId, String username){
        if(bucketListService.getUsersListById(listId, username) == null ){
            return false;
        }else{
            return true;
        }
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
        String imagePath = saveListItemImage(multipartFile, principal.getName());
        bucketListService.addItemToList(setUpBucketItemDto(request, imagePath), principal.getName());
        return "redirect:/bucketlist/details/"+ request.getParameter("listID");
    }

    private String saveListItemImage(MultipartFile file, String username){
        try{
            return storageService.store(file, username);
        }catch (Exception e){
            return "";
        }
    }

    @GetMapping("/editListItem/{listId}/{itemId}")
    public String editListItem(@PathVariable("listId") long listId, @PathVariable("itemId") long itemId, Model model, Principal principal){
        model = setUpModelForEditListItem(model, listId, itemId, principal.getName());
        return "bucketlist/add-item";
    }

    private Model setUpModelForEditListItem(Model model, Long listId, Long itemId, String username){
        BucketListDto listDto = bucketListService.getListById(listId);

        model.addAttribute("edit", true);
        model.addAttribute("username", username);
        model.addAttribute("list", listDto);

        Optional<BucketItemDto> itemDtoOpt = listDto
                .getItems()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();

        if(itemDtoOpt.isPresent()){
            model.addAttribute("item", itemDtoOpt.get());
        }

        return model;
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
    public String editBucketlist(@PathVariable Long listid, Model model, Principal principal){

        BucketListDto bucketListDto = bucketListService
                .getUsersListById(listid, principal.getName());

        if(bucketListDto == null){
            return "errors/not-authorized-error";
        }else{
            model.addAttribute("list", bucketListDto);
            model.addAttribute("titletext", "EDIT BUCKETLIST");
            model.addAttribute("username", principal.getName());
            return "bucketlist/edit";
        }

    }


    @PostMapping( MANAGE_LIST_LINK + "{listid}/edit")
    public String editBucketlist(@ModelAttribute("list") BucketListDto bucketListDto, Principal principal,
                                 @RequestParam("imagefile") MultipartFile multipartFile){

        if(!multipartFile.isEmpty()){
            String imageFilename = storageService.store(multipartFile, principal.getName());
            bucketListDto.setPhotoPath(imageFilename);
        }

        bucketListService.updateList(bucketListDto);

        return "redirect:/bucketlist/details/"+ bucketListDto.getId();
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
    public String search(@RequestParam("query") String query, @RequestParam("criterium") String criterium, Model model){

        if(criterium.equals("by_tags")){
            model.addAttribute("lists", bucketListService.getPublicBucketlistsByTag(query));
            model.addAttribute("byTags", "true");
        }else {
            model.addAttribute("lists", bucketListService.getPublicBucketlistsByUsername(query));
            model.addAttribute("byUser", "true");
        }

        return SHOW_BUCKETLISTS;
    }

    @GetMapping("/showsharedlists")
    public String showSharedListsForUser(Model model, Principal principal){
        model.addAttribute("lists", bucketListService.getBucketlistsAvailableForUser(principal.getName()));
        return SHOW_BUCKETLISTS;
    }

    @PostMapping("/markitem/{itemid}")
    public @ResponseBody ResponseEntity markItem(Principal principal, @PathVariable("itemid") Long itemId){
        boolean operationState = false;

        if(principal != null){
            operationState = bucketListService.markItem(itemId, principal.getName());
        }

        if(operationState){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/unmarkitem/{itemid}")
    public @ResponseBody ResponseEntity unmarkItem(Principal principal, @PathVariable("itemid") Long itemId){
        boolean operationState = false;

        if(principal != null){
            operationState = bucketListService.markItem(itemId, principal.getName());
        }

        if(operationState){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // todo add default images 270x170px

    @RequestMapping(value = "/getImage/{nickname}/{photoPath:.+}")
    @ResponseBody
    public byte[] getImage(@PathVariable String photoPath, @PathVariable String nickname){

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
            //throw new StorageException("Trouble serving content..." + e);
        }
        return null;
    }
}
