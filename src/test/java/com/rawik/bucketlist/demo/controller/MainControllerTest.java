package com.rawik.bucketlist.demo.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainControllerTest {

    MainController mainController;

    @Before
    public void setUp(){
        mainController = new MainController();
    }

    @Test
    public void getHome() {
        String view = mainController.getHome();
        assertEquals("homepage", view);
    }
}