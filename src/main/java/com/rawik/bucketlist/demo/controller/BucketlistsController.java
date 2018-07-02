package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.repository.UserRepository;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebResult;
import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Controller
public class BucketlistsController {

    @Autowired
    UserService userService;

    @Autowired
    BucketListService bucketListService;

    @Autowired
    BucketListMapper bucketListMapper;

    @Autowired
    BucketItemMapper bucketItemMapper;

    @Autowired
    private HttpServletRequest servletRequest;

    @GetMapping("/bucketlists")
    public String manageBucketlists(Model model, Principal principal){

        User user = userService.findByUsername(principal.getName());

        model.addAttribute("lists", user.getBucketLists());

        return "bucketlist/manage-bucketlists";
    }

    @PostMapping("/bucketlists")
    public String saveBucketlist(@ModelAttribute("list") BucketListDto listDto){

        bucketListService.saveList(listDto);

        return "redirect:/bucketlists";
    }

    @GetMapping("/bucketlist/{id}")
    public String showBucketlist(@ModelAttribute("id") Long id, Model model){

        BucketListDto listDto = bucketListMapper.bucketListToDto(
                bucketListService.getListById(id)
        );

        model.addAttribute("list", listDto);

        return "bucketlist/show-list";

    }

    @PostMapping("/addListItem")
    public String addListItem(WebRequest request, Principal principal){

        BucketItemDto itemDto = new BucketItemDto();

        itemDto.setListId(Long.valueOf(request.getParameter("listID")));
        itemDto.setName(request.getParameter("name"));
        itemDto.setDescription(request.getParameter("description"));
        itemDto.setPrice(Long.valueOf(request.getParameter("price")));
        itemDto.setAddedDate(new Date());
        itemDto.setImage(request.getParameter("image"));

        User user = userService.findByUsername(principal.getName());

        Optional<BucketList> bucketListOptional = user.getBucketLists().stream().filter(list -> list.getId().equals(itemDto.getListId())).findFirst();

        if(bucketListOptional.isPresent()){
            BucketList bucketList = bucketListOptional.get();
            bucketList.getItems().add(bucketItemMapper.dtoToBucketItem(itemDto));
        }

        userService.updateBucketLists(user);

        return "redirect:/bucketlist/" + itemDto.getListId();
    }

}
