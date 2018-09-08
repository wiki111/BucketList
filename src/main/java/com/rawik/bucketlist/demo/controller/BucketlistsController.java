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
        return SHOW_USERS_BUCKETLISTS;
    }

    @GetMapping("/bucketlists")
    public String showPublicBucketlists(Model model){
        model.addAttribute(
                "lists",
                bucketListService.getPublicBucketlists());
        return SHOW_BUCKETLISTS;
    }

    @GetMapping(MANAGE_LIST_LINK + "{id}")
    public String showUsersBucketlistForManagement(
            @ModelAttribute("id") Long id, Model model, Principal principal){

        BucketListDto listDto = userService
                .getUsersListById(id, principal.getName());
        model.addAttribute("list", listDto);
        model.addAttribute("username", principal.getName());

        return "bucketlist/show-list-details-manage";
    }

    @GetMapping("/bucketlist/details/{id}")
    public String showPublicBucketlistDetails(
            @PathVariable("id") Long id, Model model){

        BucketListDto bucketListDto = bucketListService.getListById(id);
        model.addAttribute("list", bucketListDto);

        UserDto user = userService.findByUserId(bucketListDto.getUserId());
        model.addAttribute("username", user.getNickname());

        return "bucketlist/show-list-details";
    }

    @PostMapping("/addListItem")
    public String addListItem(WebRequest request, Principal principal){

        bucketListService.addItemToList(
                setUpBucketItemDto(request), principal.getName());

        return "redirect:" + MANAGE_LIST_LINK + request.getParameter("listID");
    }

    private BucketItemDto setUpBucketItemDto(WebRequest request){

        BucketItemDto itemDto = new BucketItemDto();
        itemDto.setListId(Long.valueOf(request.getParameter("listID")));
        itemDto.setName(request.getParameter("name"));
        itemDto.setDescription(request.getParameter("description"));

        try{
            itemDto.setPrice(Long.valueOf(request.getParameter("price")));
        }catch (NumberFormatException e){
            itemDto.setPrice(0L);
        }

        itemDto.setAddedDate(new Date());
        itemDto.setImage(request.getParameter("image"));

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
            return "bucketlist/edit";
        }

    }


    @PostMapping( MANAGE_LIST_LINK + "{listid}/edit")
    public String editBucketlist(
            @ModelAttribute("bucketlistdto") BucketListDto bucketListDto,
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

    @RequestMapping(value = "/getBucketlistImage/{nickname}/{listid}")
    @ResponseBody
    public byte[] getBucketlistImage(@PathVariable Long listid, @PathVariable String nickname){
        String filename = bucketListService.getImageForListId(listid);

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
