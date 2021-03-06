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
    public void delete_room_by_id() throws Exception {
        roomService.addNewRoom("testRoom112233");
        List<RoomVO> roomList = roomService.getRoomList();
        int count = roomList.size();
        Integer deleteId = roomMapper.getRoomByName("testRoom112233").getId();
        roomService.deleteRoomById(deleteId);
        List<RoomVO> roomListNew = roomService.getRoomList();
        int newCount = roomListNew.size();
        AssertJUnit.assertEquals(count-1,newCount);
    }

    @Test
    public void delete_room_by_id_test_already_have_player() throws Exception {
        roomService.addNewRoom("testRoom112233");
        Room testRoom = roomMapper.getRoomByName("testRoom112233");
        List<RoomVO> roomList = roomService.getRoomList();
        int count = roomList.size();

        roomService.enterRoom(testRoom.getId(),mockUser.getId());
        roomService.deleteRoomById(testRoom.getId());

        List<RoomVO> roomListNew = roomService.getRoomList();
        int newCount = roomListNew.size();
        AssertJUnit.assertEquals(count,newCount);
    }

    @Test
    public void add_new_room() throws Exception {
        List<RoomVO> roomList = roomService.getRoomList();
        int count = roomList.size();
        roomService.addNewRoom("testRoom112233");
        List<RoomVO> roomListNew = roomService.getRoomList();
        int newCount = roomListNew.size();
        AssertJUnit.assertEquals(count+1,newCount);
    }

    @Test
    public void add_new_room_exist_room_name() throws Exception {
        List<RoomVO> roomList = roomService.getRoomList();
        int count = roomList.size();
        roomService.addNewRoom("testRoom112233");
        roomService.addNewRoom("testRoom112233");
        List<RoomVO> roomListNew = roomService.getRoomList();
        int newCount = roomListNew.size();
        AssertJUnit.assertEquals(count+1,newCount);
    }

    @Test
    public void modify_room_name() throws Exception {
        String oldName = "testRoomOld";
        String newName = "testRoomNew";
        roomService.addNewRoom(oldName);
        Room testRoom = roomMapper.getRoomByName(oldName);
        roomService.modifyRoomName(testRoom.getId(),newName);
        testRoom = roomMapper.getRoomByName(newName);
        AssertJUnit.assertEquals(testRoom.getRoomName(),newName);
        AssertJUnit.assertNotNull(testRoom);
    }

    @Test
    public void modify_room_name_already_exist() throws Exception {
        String oldName = "testRoomOld";
        String newName = "testRoomNew";
        roomService.addNewRoom(oldName);
        roomService.addNewRoom(newName);
        Room testRoom = roomMapper.getRoomByName(oldName);
        Integer testRoomId = testRoom.getId();
        roomService.modifyRoomName(testRoomId,newName);
        RoomVO testRoomVO = roomMapper.getRoomById(testRoomId);
        AssertJUnit.assertEquals(oldName,testRoomVO.getRoomName());
    }

    @Test
    public void get_room_list() throws Exception {
        List<RoomVO> roomList = roomService.getRoomList();
        AssertJUnit.assertNotNull(roomList);
    }

    @Test
    public void get_room_by_id() throws Exception {
        RoomVO roomNull = roomService.getRoomById(-100);
        AssertJUnit.assertNull(roomNull);
        RoomVO room = roomService.getRoomById(10);
        AssertJUnit.assertNotNull(room);
        AssertJUnit.assertEquals(10,(int)room.getId());
    }
    @Test
    public void get_room_list_by_page() throws Exception {
        List<Room> roomList = roomService.getRoomListByPage(1,10);
        AssertJUnit.assertNotNull(roomList);
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
    public void exit_room() throws Exception {
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