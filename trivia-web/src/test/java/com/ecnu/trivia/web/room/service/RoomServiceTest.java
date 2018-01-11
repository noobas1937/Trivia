package com.ecnu.trivia.web.room.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.game.domain.vo.PlayerVO;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * @author Jack Chen
 * @date 2018.1.5
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class RoomServiceTest {
    @Resource
    private RoomService roomService;
    @Resource
    private SessionService sessionService;
    @Resource
    private RoomMapper roomMapper;
    private User mockUser;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private User mockUser4;

    @Before
    public void setUp() throws Exception {
        //模拟用户
        sessionService.addNewUser("test-user","123","test-user");
        sessionService.addNewUser("test-user1","123","test-user1");
        sessionService.addNewUser("test-user2","123","test-user2");
        sessionService.addNewUser("test-user3","123","test-user3");
        sessionService.addNewUser("test-user4","123","test-user4");
        mockUser = sessionService.getUserByAccount("test-user","123");
        mockUser1 = sessionService.getUserByAccount("test-user1","123");
        mockUser2 = sessionService.getUserByAccount("test-user2","123");
        mockUser3 = sessionService.getUserByAccount("test-user3","123");
        mockUser4 = sessionService.getUserByAccount("test-user4","123");

    }

    @Test
    public void getRoomList() throws Exception {
        List<RoomVO> roomList = roomService.getRoomList();
        AssertJUnit.assertNotNull(roomList);
    }

    @Test
    public void getRoomById() throws Exception {
        RoomVO roomNull = roomService.getRoomById(-100);
        AssertJUnit.assertNull(roomNull);
        RoomVO room = roomService.getRoomById(10);
        AssertJUnit.assertNotNull(room);
        AssertJUnit.assertEquals(10,(int)room.getId());
    }

    @Test
    public void enter_room_where_the_room_is_playing() throws Exception {
        roomMapper.updateRoomStatus(10,Constants.ROOM_PLAYING);
        Resp resp = roomService.enterRoom(10,mockUser.getId());
        AssertJUnit.assertEquals(HttpRespCode.ROOM_PLAYING.getCode(),resp.getResCode());
    }

    @Test
    public void enter_room_where_the_room_is_waiting() throws Exception{
        roomMapper.updateRoomStatus(10,Constants.ROOM_WAITING);
        roomService.enterRoom(10,mockUser.getId());
        RoomVO roomVO = roomService.getRoomById(10);
        List<PlayerVO> players = roomVO.getPlayerList();
        AssertJUnit.assertNotNull(players);
        for (PlayerVO p : players) {
            if(Objects.equals(p.getUserId(), mockUser.getId())){
                return;
            }
        }
        AssertJUnit.fail();
    }

    @Test
    public void enter_room_where_user_is_already_in() throws Exception{
        //函数会直接返回加入成功，促使前台跳转到原来的房间
        roomMapper.updateRoomStatus(10,Constants.ROOM_WAITING);
        roomService.enterRoom(10,mockUser.getId());
        Resp resp = roomService.enterRoom(10,mockUser.getId());
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void enter_room_where_the_room_is_full() throws Exception{
        //函数会直接返回加入成功，促使前台跳转到原来的房间
        roomMapper.updateRoomStatus(10,Constants.ROOM_WAITING);
        roomService.enterRoom(10,mockUser1.getId());
        roomService.enterRoom(10,mockUser2.getId());
        roomService.enterRoom(10,mockUser3.getId());
        roomService.enterRoom(10,mockUser4.getId());
        Resp resp = roomService.enterRoom(10,mockUser.getId());
        AssertJUnit.assertEquals(HttpRespCode.ROOM_FULL.getCode(),resp.getResCode());
    }

    @Test
    public void exitRoom() throws Exception {
        //退出未在游戏中的房间 -> 成功退出房间
        roomService.enterRoom(10,mockUser.getId());
        roomService.exitRoom(mockUser.getId());
        RoomVO roomVOEmpty = roomService.getRoomById(10);
        List<PlayerVO> playersEmpty = roomVOEmpty.getPlayerList();
        for (PlayerVO p2 : playersEmpty) {
            if(p2.getUserId()==mockUser.getId()){
                AssertJUnit.fail();
            }
        }

        //退出正在游戏中的房间 -> 返回错误消息
        roomService.enterRoom(10,mockUser.getId());
        roomMapper.updateRoomStatus(10, Constants.ROOM_PLAYING);
        Resp resp = roomService.exitRoom(mockUser.getId());
        AssertJUnit.assertEquals(HttpRespCode.ROOM_PLAYING.getCode(),resp.getResCode());
    }

}