package com.rawik.bucketlist.demo.controller;

import com.rawik.bucketlist.demo.dto.BucketItemDto;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.mapper.BucketItemMapper;
import com.rawik.bucketlist.demo.mapper.BucketListMapper;
import com.rawik.bucketlist.demo.model.BucketItem;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.model.User;
import com.rawik.bucketlist.demo.service.BucketListService;
import com.rawik.bucketlist.demo.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    WebRequest request;

    BucketlistsController bucketlistsController;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bucketlistsController =
                new BucketlistsController(userService, bucketListService);
    }

    @Test
    public void showUsersBucketlistsTest() {

        Set<BucketListDto> lists = new HashSet<>();

        when(principal.getName()).thenReturn("testname");
        when(userService.getUserLists(anyString())).thenReturn(lists);

        ArgumentCaptor<Set<BucketListDto>> listCaptor = ArgumentCaptor.forClass(Set.class);

        String view = bucketlistsController.showUsersBucketlists(model, principal);

        assertEquals("bucketlist/show-users-bucketlists", view);
        verify(model, times(1)).addAttribute(eq("lists"), listCaptor.capture());
        assertEquals(lists, listCaptor.getValue());

    }

    @Test
    public void showBucketlistForManagementTest() {

        Long id = 1L;
        BucketListDto listDto = new BucketListDto();
        ArgumentCaptor<Set<BucketListDto>> listCaptor = ArgumentCaptor.forClass(Set.class);

        when(principal.getName()).thenReturn("test");
        when(userService.getUsersListById(anyLong(), anyString())).thenReturn(listDto);

        String view = bucketlistsController.showUsersBucketlistForManagement(id, model, principal);

        assertEquals("bucketlist/show-list-details-manage", view);
        verify(model, times(1)).addAttribute(eq("list"), listCaptor.capture());
        assertEquals(listDto, listCaptor.getValue());
    }

    @Test
    @Ignore
    public void addListItem() {

        when(request.getParameter("listID")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Name");
        when(request.getParameter("description")).thenReturn("Some desc");
        when(request.getParameter("price")).thenReturn("10");
        when(request.getParameter("image")).thenReturn("https://someimage.com");

        when(principal.getName()).thenReturn("Name");

        String view = bucketlistsController.addListItem(request, principal);

        ArgumentCaptor<BucketItemDto> bucketItemDtoCaptor = ArgumentCaptor.forClass(BucketItemDto.class);
        verify(bucketListService, times(1)).addItemToList(bucketItemDtoCaptor.capture(), anyString());

        BucketItemDto itemDto = bucketItemDtoCaptor.getValue();
        assertEquals("Name", itemDto.getName());
        assertEquals("Some desc", itemDto.getDescription());
        assertEquals((Long) 10L, (Long) itemDto.getPrice());
        assertEquals("https://someimage.com", itemDto.getImage());
    }


}
