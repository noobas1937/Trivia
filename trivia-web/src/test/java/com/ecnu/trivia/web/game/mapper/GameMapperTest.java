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

/**
 * @author miss guo
 * @date:2018.1.4
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class GameMapperTest {
    @Resource
    private GameMapper gameMapper;

    @Test
    public void getGameByRoomId() throws Exception {
        Integer roomid=1;
        Integer roomid1=5;
        Game res=gameMapper.getGameByRoomId(roomid);
        Game res1=gameMapper.getGameByRoomId(roomid1);
       if(res!=null){
           AssertJUnit.fail("fail ");
       }
        if(res1==null){
            AssertJUnit.fail("fail ");
        }
    }

    @Test
    public void updateGameStatus() throws Exception {
        gameMapper.updateGameStatus(23,79,1,3,1);
        Integer CurrentPlayerId=gameMapper.getGameByRoomId(5).getCurrentPlayerId();
        Integer dicenumber=gameMapper.getGameByRoomId(5).getDiceNumber();
        Integer quesid=gameMapper.getGameByRoomId(5).getQuestionId();
        Integer stage=gameMapper.getGameByRoomId(5).getStage();
        if(CurrentPlayerId!=79){
            AssertJUnit.fail("fail");
        }
        if(dicenumber!=1){
            AssertJUnit.fail("fail");
        }
        if(quesid!=3){
            AssertJUnit.fail("fail");
        }
        if(stage!=1){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void getGameByQuestionId() throws Exception {
        Game res=gameMapper.getGameByQuestionId(17);
        if(res==null){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void addGame() throws Exception {
        gameMapper.addGame(10,79);
        Game res=gameMapper.getGameByRoomId(10);
        if(res==null){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void getAppropriateReadyRoomId() throws Exception {
        Integer res=gameMapper.getAppropriateReadyRoomId(4);
        if(res!=3){
            AssertJUnit.fail("fail");
        }
    }

}