package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class MessageController {

    @Autowired
    MessageService messageService;

    @PostMapping("/send-message")
    public String sendMessage(Principal principal, @RequestParam("receiver") String receiver, @RequestParam("message") String message){
        messageService.sendMessage(principal.getName(), receiver, message);
        return "redirect:/profile";
    }

    @GetMapping("/send-message")
    public String sendMessage(){
        return "message/sendform";
    }

    @GetMapping("/user/sent")
    public String getSentMessages(Principal principal, Model model){
        model.addAttribute("messages", messageService.getSentMessages(principal.getName()));
        model.addAttribute("showReceived", false);
        return "user/show-messages";
    }

    @GetMapping("/user/received")
    public String getReceivedMessages(Principal principal, Model model){
        model.addAttribute("messages", messageService.getReceivedMessages(principal.getName()));
        model.addAttribute("showReceived", true);
        return "user/show-messages";
    }

}
