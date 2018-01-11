package com.ecnu.trivia.web.message.service;

import com.ecnu.trivia.web.message.communicator.HallCommunicator;
import com.ecnu.trivia.web.message.communicator.WebSocketCommunicator;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.room.controller.RoomController;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.room.mapper.RoomMapperTest;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class MessageServiceTest {
    @Resource
    private MessageService messageService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private RoomService roomService;
    private User mockUser;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private User mockUser4;

    @Before
    public void setUp() throws Exception {
        //模拟用户
        userMapper.addNewUser("alucardtest","12345678","xiaotest",null);
        userMapper.addNewUser("test-user1","123","test-user1",null);
        userMapper.addNewUser("test-user2","123","test-user2",null);
        userMapper.addNewUser("test-user3","123","test-user3",null);
        userMapper.addNewUser("test-user4","123","test-user4",null);
        mockUser = userMapper.getUserByAccount("alucardtest","12345678");
        mockUser1 = userMapper.getUserByAccount("test-user1","123");
        mockUser2 = userMapper.getUserByAccount("test-user2","123");
        mockUser3 = userMapper.getUserByAccount("test-user3","123");
        mockUser4 = userMapper.getUserByAccount("test-user4","123");

        HallCommunicator.ONLINE_USER.put(mockUser.getId(),new HallCommunicator());
        WebSocketCommunicator.ONLINE_USER.put(mockUser.getId(),new WebSocketCommunicator());
        HallCommunicator.ONLINE_USER.put(mockUser1.getId(),new HallCommunicator());
        WebSocketCommunicator.ONLINE_USER.put(mockUser1.getId(),new WebSocketCommunicator());
        HallCommunicator.ONLINE_USER.put(mockUser2.getId(),new HallCommunicator());
        WebSocketCommunicator.ONLINE_USER.put(mockUser2.getId(),new WebSocketCommunicator());
        HallCommunicator.ONLINE_USER.put(mockUser3.getId(),new HallCommunicator());
        WebSocketCommunicator.ONLINE_USER.put(mockUser3.getId(),new WebSocketCommunicator());
        HallCommunicator.ONLINE_USER.put(mockUser4.getId(),new HallCommunicator());
        WebSocketCommunicator.ONLINE_USER.put(mockUser4.getId(),new WebSocketCommunicator());
    }

    @Test
    public void send_msg_to_all_terminal() throws Exception {
        messageService.sendToAllTerminal("hello");
    }

    @Test
    public void send_msg_to_all_hall_terminal() throws Exception {
        messageService.sendToAllHallTerminal("hello");
    }

    @Test
    public void send_msg_to_all_terminal_with_out_myself() throws Exception {
        messageService.sendToAllHallTerminal("hello",mockUser.getId());
    }

    @Test
    public void refresh_ui() throws Exception {
        roomService.enterRoom(1,mockUser1.getId());
        roomService.enterRoom(1,mockUser2.getId());
        roomService.enterRoom(1,mockUser3.getId());
        roomService.enterRoom(1,mockUser4.getId());
        roomMapper.updateRoomStatus(1, Constants.ROOM_WAITING);
        messageService.refreshUI(1);
    }

    @Test
    public void refresh_ui_with_no_param() throws Exception {
        messageService.refreshUI(null);
    }

    @Test
    public void refresh_hall_ui() throws Exception {
        messageService.refreshHall();
    }
}