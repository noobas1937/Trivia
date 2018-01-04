package com.ecnu.trivia.web.game.mapper;

import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class GameMapperTest {
    @Resource
    private GameMapper gameMapper;

    @Test
    public void getGameByRoomId() throws Exception {
        Integer roomid=1;
        Integer roomid1=5;
       Game res=gameMapper.getGameByRoomId(roomid);
        Game res1=gameMapper.getGameByRoomId(roomid);
       if(res!=null){
           AssertJUnit.fail("fail ");
       }
        if(res1==null){
            AssertJUnit.fail("fail ");
        }
    }

    @Test
    public void updateGameStatus() throws Exception {
        gameMapper.updateGameStatus();


    }

    @Test
    public void getGameByQuestionId() throws Exception {
    }

    @Test
    public void addGame() throws Exception {
    }

    @Test
    public void getAppropriateReadyRoomId() throws Exception {
    }

}