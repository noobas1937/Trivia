package com.ecnu.trivia.web.game.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class GameServiceTest {

    @Resource
    private GameService gameService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getRoomByUserId() throws Exception {
        gameService.getRoomByUserId(2);
    }

    @Test
    public void rollDice() throws Exception {
        boolean res = gameService.rollDice(2);
        AssertJUnit.assertEquals(false,res);
    }

    @Test
    public void checkReady() throws Exception {
        gameService.checkReady(2,1);
    }

    @Test
    public void getAppropriateReadyRoomId() throws Exception {
        Integer res = gameService.getAppropriateReadyRoomId();
    }

    @Test
    public void refreshUserRoom() throws Exception {
        gameService.refreshUserRoom(2);
    }

}