package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.dto.UserDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class BucketlistsController {

    private UserService userService;
    private BucketListService bucketListService;
    private BucketListMapper bucketListMapper;
    private BucketItemMapper bucketItemMapper;

    private static final String MANAGE_LIST_LINK = "/user/bucketlist/manage/";
    private static final String SHOW_USERS_BUCKETLISTS = "bucketlist/show-users-bucketlists";
    private static final String SHOW_BUCKETLISTS = "bucketlist/show-bucketlists";

    public BucketlistsController(UserService userService, BucketListService bucketListService, BucketListMapper bucketListMapper, BucketItemMapper bucketItemMapper) {
        this.userService = userService;
        this.bucketListService = bucketListService;
        this.bucketListMapper = bucketListMapper;
        this.bucketItemMapper = bucketItemMapper;
    }

    @GetMapping("/user/bucketlists")
    public String showUsersBucketlists(Model model, Principal principal){

        model.addAttribute("lists", userService.getUserLists(principal.getName()));

        return SHOW_USERS_BUCKETLISTS;
    }

    @GetMapping("/bucketlists")
    public String showBucketlists(Model model){
        model.addAttribute("lists", bucketListService.getPublicBucketlists());
        return SHOW_BUCKETLISTS;
    }

    @GetMapping(MANAGE_LIST_LINK + "{id}")
    public String showBucketlistForManagement(@ModelAttribute("id") Long id, Model model, Principal principal){

        BucketListDto listDto = userService.getUsersListById(id, principal.getName());

        model.addAttribute("list", listDto);

        return "bucketlist/show-list-details-manage";

    }

    @GetMapping("/bucketlist/details/{id}")
    public String showBucketlistDetails(@PathVariable("id") Long id, Model model){

        BucketList bucketList = bucketListService.getListById(id);
        BucketListDto bucketListDto = bucketListMapper.bucketListToDto(bucketList);
        model.addAttribute("list", bucketListDto);

        UserDto user = userService.findByUserId(bucketListDto.getUserId());
        model.addAttribute("username", user.getEmail());

        return "bucketlist/show-list-details";

    }

    @PostMapping("/addListItem")
    public String addListItem(WebRequest request, Principal principal){

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

        User user = userService.findByUsername(principal.getName());

        Optional<BucketList> bucketListOptional = user.getBucketLists().stream().filter(list -> list.getId().equals(itemDto.getListId())).findFirst();

        if(bucketListOptional.isPresent()){
            BucketList bucketList = bucketListOptional.get();
            bucketList.getItems().add(bucketItemMapper.dtoToBucketItem(itemDto));
        }

        userService.updateBucketLists(user);

        return "redirect:" + MANAGE_LIST_LINK + itemDto.getListId();
    }

    @GetMapping( MANAGE_LIST_LINK + "{listid}/edit" )
    public String editBucketlist(@PathVariable Long listid, Model model, Principal principal){

        BucketList bucketList = bucketListService.getListById(listid);

        if(bucketList.getUser().getEmail() == principal.getName()){
            BucketListDto bucketListDto = bucketListMapper.bucketListToDto(bucketList);
            model.addAttribute("list", bucketListDto);
            return "bucketlist/edit";
        }

        return "errors/not-authorized-error";
    }

    @PostMapping( MANAGE_LIST_LINK + "{listid}/edit")
    public String editBucketlist(@ModelAttribute("bucketlistdto") BucketListDto bucketListDto, Model model, Principal principal){

        bucketListService.updateList(bucketListDto);

        return "redirect:" + MANAGE_LIST_LINK + bucketListDto.getId();

    }

    @GetMapping("/bucketlist/delete/{id}")
    public String deleteBucketlist(@PathVariable Long id, Principal principal){
        bucketListService.dropList(id, principal.getName());
        return "redirect:/bucketlists";
    }

    @GetMapping("/user/bucketlist/{listid}/item/{itemid}/delete")
    public String deleteBucketlistItem(@PathVariable Long listid, @PathVariable Long itemid, Principal principal){
        bucketListService.dropListItem(listid, itemid, principal.getName());
        return "redirect:" + MANAGE_LIST_LINK + listid;
    }

    @PostMapping("/search")
    public String showPublicBucketlistsByTags(@RequestParam("query") String query, Model model){

        List<String> tags = bucketListMapper.tagStringToList(query);
        model.addAttribute("lists", bucketListService.getPublicBucketlistsByTag(tags));

        return SHOW_BUCKETLISTS;
    }
}
