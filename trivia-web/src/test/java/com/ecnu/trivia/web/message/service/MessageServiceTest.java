package com.ecnu.trivia.web.message.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class MessageServiceTest {
    @Resource
    private MessageService messageService;

    @Test
    public void sendToAllTerminal() throws Exception {
        messageService.sendToAllTerminal("hello");
    }

    @Test
    public void sendToAllHallTerminal() throws Exception {
        messageService.sendToAllHallTerminal("hello");
    }

    @Test
    public void sendToAllHallTerminal1() throws Exception {
        messageService.sendToAllHallTerminal("hello",1);
    }

    @Test
    public void refreshUI() throws Exception {
        messageService.refreshUI(1);
    }

    @Test
    public void refresh_ui_with_no_param() throws Exception {
        messageService.refreshUI(null);
    }

    @Test
    public void refreshHall() throws Exception {
        messageService.refreshHall();
    }
}