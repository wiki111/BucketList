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

        Long id = 1L;
        BucketListDto listDto = new BucketListDto();
        ArgumentCaptor<Set<BucketListDto>> listCaptor = ArgumentCaptor.forClass(Set.class);

        when(principal.getName()).thenReturn("test");
        when(userService.getUsersListById(anyLong(), anyString())).thenReturn(listDto);

        String view = bucketlistsController.showBucketlist(id, model, principal);

        assertEquals("bucketlist/show-list", view);
        verify(model, times(1)).addAttribute(eq("list"), listCaptor.capture());
        assertEquals(listDto, listCaptor.getValue());
    }

    @Test
    public void addListItem() {

        when(request.getParameter("listID")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Name");
        when(request.getParameter("description")).thenReturn("Some desc");
        when(request.getParameter("price")).thenReturn("10");
        when(request.getParameter("image")).thenReturn("https://someimage.com");

        User user = new User();
        BucketList baselist = new BucketList();
        baselist.setId(1L);
        user.getBucketLists().add(baselist);
        BucketItem baseitem = new BucketItem();
        baseitem.setName("Name");
        baseitem.setDescription("Some desc");
        baseitem.setImage("https://someimage.com");
        baseitem.setPrice(10L);

        when(bucketItemMapper.dtoToBucketItem(any(BucketItemDto.class))).thenReturn(baseitem);

        when(principal.getName()).thenReturn("Name");
        when(userService.findByUsername(anyString())).thenReturn(user);

        String view = bucketlistsController.addListItem(request, principal);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userService, times(1)).updateBucketLists(userCaptor.capture());

        Optional<BucketList> bucketlistOpt = userCaptor.getValue().getBucketLists().stream().filter(list -> list.getId().equals(1L)).findFirst();
        BucketList bucketlist = bucketlistOpt.get();
        Optional<BucketItem> bucketitemOpt = bucketlist.getItems().stream().filter(item -> item.getName().equals("Name")).findFirst();
        BucketItem bucketitem = bucketitemOpt.get();

        assertNotNull(bucketitem);
        assertEquals(bucketitem.getDescription(), "Some desc");
        assertEquals(bucketitem.getPrice(), Long.valueOf(10));
        assertEquals(bucketitem.getImage(), "https://someimage.com");

        assertEquals("redirect:/bucketlist/manage/1", view);
    }


}
