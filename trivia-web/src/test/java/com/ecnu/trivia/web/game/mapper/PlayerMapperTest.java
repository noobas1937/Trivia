package com.ecnu.trivia.web.game.mapper;
/*author:miss guo
  date:2018.1.4*/
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import org.junit.Before;
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
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoomMapper roomMapper;
    private User mockUser;
    private Room mockRoom;
    private Player mockPlayer;

    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("test-user","12345678","nickName",null);
        mockUser = userMapper.getUserByAccount("test-user","12345678");
        roomMapper.addRoomByName("test-room");
        mockRoom = roomMapper.getRoomByName("test-room");
        playerMapper.addPlayer(mockRoom.getId(),mockUser.getId());
        mockPlayer =playerMapper.getPlayerByUserId(mockUser.getId());

    }

    @Test
    public void set_up_user_state() throws Exception {
        playerMapper.setupUserState(mockUser.getId(), Constants.PLAYER_READY);
        Player res=playerMapper.getPlayerByUserId(mockUser.getId());
        AssertJUnit.assertEquals(Constants.PLAYER_READY,(int)res.getStatus());
    }

    @Test
    public void add_player() throws Exception {
        playerMapper.removePlayer(mockUser.getId());
        playerMapper.addPlayer(mockRoom.getId(),mockUser.getId());
        Player player=playerMapper.getPlayerByUserId(mockUser.getId());
        AssertJUnit.assertNotNull(player);

    }

    @Test
    public void remove_player() throws Exception {
        playerMapper.removePlayer(mockUser.getId());
        Player player=playerMapper.getPlayerByUserId(mockUser.getId());
        AssertJUnit.assertNull(player);
    }

    @Test
    public void get_player_count() throws Exception {
       Integer count= playerMapper.getPlayerCount(mockRoom.getId());
        AssertJUnit.assertEquals(1,(int)count);

    }

    @Test
    public void get_players() throws Exception {
        List player=playerMapper.getPlayers(mockPlayer.getId());
        AssertJUnit.assertNotNull(player);
    }

    @Test
    public void get_player_by_user_id() throws Exception {
        Player player=playerMapper.getPlayerByUserId(mockUser.getId());
        AssertJUnit.assertNotNull(player);
    }

    @Test
    public void get_question_count() throws Exception {
        Integer res=playerMapper.getQuestionCount();
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void update_player() throws Exception {
        playerMapper.updatePlayer(mockPlayer.getId(),4,29,Constants.PLAYER_GAMING_FREE);
        Player player=playerMapper.getPlayerByUserId(mockUser.getId());
        AssertJUnit.assertEquals(4,(int)player.getBalance());
        AssertJUnit.assertEquals(29,(int)player.getPosition());
        AssertJUnit.assertEquals(Constants.PLAYER_GAMING_FREE,(int)player.getStatus());
    }

    @Test
    public void get_not_ready_player() throws Exception {
        List player=playerMapper.getNotReadyPlayer(mockRoom.getId());
        AssertJUnit.assertNotNull(player);
    }

}