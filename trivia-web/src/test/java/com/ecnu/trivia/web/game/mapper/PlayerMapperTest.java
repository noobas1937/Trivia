package com.ecnu.trivia.web.game.mapper;
/*author:miss guo
  date:2018.1.4*/
import com.ecnu.trivia.web.game.domain.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class PlayerMapperTest {
    @Resource
    private PlayerMapper playerMapper;
    @Test
    public void setupUserState() throws Exception {
        playerMapper.setupUserState(3,0);
        Integer res=playerMapper.getPlayerByUserId(3).getStatus();
        if(res!=0){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void addPlayer() throws Exception {
        playerMapper.addPlayer(30,8);
        Player player=playerMapper.getPlayerByUserId(8);
        if(player==null){
            AssertJUnit.fail("fail");
        }

    }

    @Test
    public void removePlayer() throws Exception {
        playerMapper.removePlayer(79);
        Player player=playerMapper.getPlayerByUserId(79);
        if(player!=null){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void getPlayerCount() throws Exception {
       Integer player= playerMapper.getPlayerCount(3);
       if(player!=2){
           AssertJUnit.fail("fail");
       }

    }

    @Test
    public void getPlayers() throws Exception {
        List player=playerMapper.getPlayers(79);
        if(player.size()<0){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void getPlayerByUserId() throws Exception {
        Player player=playerMapper.getPlayerByUserId(4);
        if(player==null){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void getQuestionCount() throws Exception {
        Integer res=playerMapper.getQuestionCount();
        if(res<0){
            AssertJUnit.fail("fail");
        }
    }

    @Test
    public void updatePlayer() throws Exception {
        playerMapper.updatePlayer(79,4,29,1);
        Integer bal=playerMapper.getPlayerByUserId(4).getBalance();
        Integer pos=playerMapper.getPlayerByUserId(4).getPosition();
        Integer sta=playerMapper.getPlayerByUserId(4).getStatus();
        if(bal!=4){
            AssertJUnit.fail("fail");
        }
        if(pos!=29){
            AssertJUnit.fail("fail");
        }
        if(sta!=1){
            AssertJUnit.fail("fail");
        }


    }

    @Test
    public void getNotReadyPlayer() throws Exception {
        List player=playerMapper.getNotReadyPlayer(3);
        if(player.size()<0){
            AssertJUnit.fail("fail");
        }
    }

}