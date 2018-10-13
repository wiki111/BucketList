package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.exceptions.StorageException;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.StorageService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;

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
    public ModelAndView addNewBucketlist(@ModelAttribute("list") @Valid BucketListDto listDto,  BindingResult result, @RequestParam("imagefile") MultipartFile imagefile,
                                   Principal principal){
        String imagePath = "";
        try {
            imagePath = saveImage(imagefile, principal.getName(), listDto.getId());
        }catch (StorageException e){
            result.rejectValue("photoPath", "Image not valid : " + e.getMessage());
        }

        if(result.hasErrors()){
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("username", principal.getName());
            modelMap.put("list", listDto);
            modelMap.put("titletext", "Add new list");
            return new ModelAndView("bucketlist/add-list", "model", modelMap);
        }else{
            BucketList bucketList = bucketListService.saveList(setUpBucketlistDto(listDto, imagePath, principal.getName()));
            return new ModelAndView("redirect:/bucketlist/details/"+bucketList.getId());
        }
    }

    private BucketListDto setUpBucketlistDto(BucketListDto listDto, String imagePath, String username){
        User user = userService.findByUsername(username);
        listDto.setUserId(user.getUserId());
        listDto.setCreationDate(new Date());
        if(imagePath != null) listDto.setPhotoPath(imagePath);
        return listDto;
    }

    private String saveImage(MultipartFile imageFile, String username, Long listId){
        if(!imageFile.isEmpty()){
            try {
                storageService.deleteFile(username, bucketListService.getCurrentListImageName(listId));
            }catch (Exception e){
                throw new StorageException(e.getMessage());
            }
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
            model.addAttribute("idsOfMarkedItems", bucketListService.getIdsOfItemsMarkedByUser(principal.getName()));
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
        model.addAttribute("theListId",list.getId());
        model.addAttribute("item", new BucketItemDto());
        model.addAttribute("username", principal.getName());
        return "bucketlist/add-item";
    }

    @PostMapping("/addListItem")
    public ModelAndView addListItem(@ModelAttribute("item") @Valid BucketItemDto item, BindingResult result,
                              Principal principal, @RequestParam("listImage") MultipartFile multipartFile,
                                    @RequestParam("theListId") Long listId){

        String imagePath = "";
        ModelAndView modelAndView;

        try{
            imagePath = saveListItemImage(multipartFile, principal.getName(), String.valueOf(listId));
            item.setImage(imagePath);
        }catch (StorageException e){
            result.rejectValue("image", "Image file couldn't be stored. Please choose another. Details : " + e.getMessage());
        }

        if(result.hasErrors()){
            modelAndView = new ModelAndView("bucketlist/add-item");
            modelAndView.getModel().put("list", userService.getUsersListById(listId, principal.getName()));
            modelAndView.getModel().put("item", item);
            modelAndView.getModel().put("username", principal.getName());
        }else {
            item.setListId(listId);
            bucketListService.addItemToList(item, principal.getName());
            modelAndView = new ModelAndView("redirect:/bucketlist/details/" + listId);
        }

        return modelAndView;


    }

    private String saveListItemImage(MultipartFile file, String username, String itemId){
        try{
            try{
                storageService.deleteFile(username, bucketListService.getCurrentItemImageName(Long.valueOf(itemId)));
            }catch (Exception e){}
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
    public String editBucketlist(@ModelAttribute("list") BucketListDto bucketListDto, Principal principal, @RequestParam("imagefile") MultipartFile multipartFile){
        bucketListDto.setPhotoPath(saveImage(multipartFile, principal.getName(), bucketListDto.getId()));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/deleteList/{listId}")
    public String deleteList(@PathVariable Long listId, Principal principal){
        bucketListService.dropList(listId, principal.getName());
        return "redirect:/user/bucketlists";
    }

    @PostMapping("/deleteItem/{listId}/{itemId}")
    public String deleteItem(@PathVariable Long listId, @PathVariable Long itemId, Principal principal){
        bucketListService.dropListItem(listId, itemId, principal.getName());
        return "redirect:/bucketlist/details/" + listId;
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
