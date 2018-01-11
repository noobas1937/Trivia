package com.ecnu.trivia.web.room.mapper;
//Author:guo
//Date:2018.1.4
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
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
public class RoomMapperTest {
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PlayerMapper playerMapper;

    private User mockUser;
    private Player mockPlayer;
    private Room mockRoom;
    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("testUser","12345678","test",null);
        mockUser = userMapper.getUserByAccount("testUser","12345678");
        playerMapper.addPlayer(20,mockUser.getId());
        mockPlayer = playerMapper.getPlayerByUserId(mockUser.getId());
        roomMapper.addRoomByName("test-room");
        mockRoom = roomMapper.getRoomByName("test-room");

    }

    @Test
    public void get_room_list() throws Exception {
        List res=roomMapper.getRoomList();
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void get_room_by_id() throws Exception {
        RoomVO res= roomMapper.getRoomById(mockRoom.getId());
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void get_room_by_player_id() throws Exception {
        Room res= roomMapper.getRoomByPlayerID(mockPlayer.getId());
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void get_room_by_user_id() throws Exception {
        Room res= roomMapper.getRoomByUserID(mockUser.getId());
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void update_room_status() throws Exception {
        roomMapper.updateRoomStatus(mockRoom.getId(), Constants.ROOM_PLAYING);
        Integer roomStatus=roomMapper.getRoomById(1).getStatus();
        AssertJUnit.assertEquals(Constants.ROOM_PLAYING,(int)roomStatus);
    }

}