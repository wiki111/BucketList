package com.rawik.bucketlist.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rawik.bucketlist.demo.dto.BucketListDto;
import com.rawik.bucketlist.demo.model.BucketList;
import com.rawik.bucketlist.demo.repository.BucketListRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class BucketlistControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    BucketListRepository bucketListRepository;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Transactional
    public void testEditBucketlist() throws Exception {

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();

        formParams.add("id", "1");
        formParams.add("userId", "1");
        formParams.add("name","another name");
        formParams.add("description", "some other desc");
        formParams.add("tags", "another tag");
        formParams.add("tags", "yet another tag");

        this.mockMvc.perform(post("/bucketlist/manage/1/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formParams)).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/bucketlist/show-list"));

        Optional<BucketList> bucketlistOpt = bucketListRepository.findById(1L);

        if(bucketlistOpt.isPresent()){
            BucketList savedBucketlist = bucketlistOpt.get();

            assertEquals(savedBucketlist.getName(), "another name");
            assertEquals(savedBucketlist.getDescription(), "some other desc");
            assertTrue(savedBucketlist.getTags().contains("another tag"));
            assertTrue(savedBucketlist.getTags().contains("yet another tag"));
            assertTrue(savedBucketlist.getTags().size() == 2);

        }

    }

}
