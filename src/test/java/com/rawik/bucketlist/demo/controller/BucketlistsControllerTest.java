package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BucketlistsControllerTest {

    @Mock
    UserService userService;

    @Mock
    BucketListService bucketListService;

    @Mock
    BucketListMapper bucketListMapper;

    @Mock
    BucketItemMapper bucketItemMapper;

    @Mock
    Model model;

    @Mock
    Principal principal;

    BucketlistsController bucketlistsController;


    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        bucketlistsController =
                new BucketlistsController(userService, bucketListService, bucketListMapper, bucketItemMapper);
    }

    @Test
    public void manageBucketlists() {

        Set<BucketListDto> lists = new HashSet<>();

        when(principal.getName()).thenReturn("testname");
        when(userService.getUserLists(anyString())).thenReturn(lists);

        ArgumentCaptor<Set<BucketListDto>> listCaptor = ArgumentCaptor.forClass(Set.class);

        String view = bucketlistsController.manageBucketlists(model, principal);

        assertEquals("bucketlist/manage-bucketlists", view);
        verify(model, times(1)).addAttribute(eq("lists"), listCaptor.capture());
        assertEquals(lists, listCaptor.getValue());

    }

    @Test
    public void showBucketlist() {
    }

    @Test
    public void addListItem() {
    }
}