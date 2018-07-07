package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Controller
public class BucketlistsController {

    private UserService userService;
    private BucketListService bucketListService;
    private BucketListMapper bucketListMapper;
    private BucketItemMapper bucketItemMapper;

    public BucketlistsController(UserService userService, BucketListService bucketListService, BucketListMapper bucketListMapper, BucketItemMapper bucketItemMapper) {
        this.userService = userService;
        this.bucketListService = bucketListService;
        this.bucketListMapper = bucketListMapper;
        this.bucketItemMapper = bucketItemMapper;
    }

    @GetMapping("/bucketlists")
    public String manageBucketlists(Model model, Principal principal){

        model.addAttribute("lists", userService.getUserLists(principal.getName()));

        return "bucketlist/manage-bucketlists";
    }

    @GetMapping("/bucketlist/manage/{id}")
    public String showBucketlist(@ModelAttribute("id") Long id, Model model, Principal principal){

        BucketListDto listDto = userService.getUsersListById(id, principal.getName());

        model.addAttribute("list", listDto);

        return "bucketlist/show-list";

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

        return "redirect:/bucketlist/manage/" + itemDto.getListId();
    }

    @GetMapping("/bucketlist/manage/{listid}/edit")
    public String editBucketlist(@PathVariable Long listid, Model model, Principal principal){

        BucketList bucketList = bucketListService.getListById(listid);

        if(bucketList.getUser().getEmail() == principal.getName()){
            BucketListDto bucketListDto = bucketListMapper.bucketListToDto(bucketList);
            model.addAttribute("list", bucketListDto);
            return "bucketlist/edit";
        }

        return "errors/not-authorized-error";
    }

    @PostMapping("/bucketlist/manage/{listid}/edit")
    public String editBucketlist(@ModelAttribute("bucketlistdto") BucketListDto bucketListDto, Model model, Principal principal){

        bucketListService.updateList(bucketListDto);

        return "redirect:/bucketlist/manage/" + bucketListDto.getId();

    }

    @GetMapping("/bucketlist/delete/{id}")
    public String deleteBucketlist(@PathVariable Long id, Principal principal){
        bucketListService.dropList(id, principal.getName());
        return "redirect:/bucketlists";
    }

    @GetMapping("/bucketlist/{listid}/item/{itemid}/delete")
    public String deleteBucketlistItem(@PathVariable Long listid, @PathVariable Long itemid, Principal principal){
        bucketListService.dropListItem(listid, itemid, principal.getName());
        return "redirect:/bucketlist/manage/" + listid;
    }
}
