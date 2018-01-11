package com.ecnu.trivia.web.room.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.rbac.controller.SessionController;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class RoomControllerTest {
    @Resource
    private RoomController roomController;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private RoomService roomService;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private SessionService sessionService;
    @Resource
    private MockHttpSession session;
    private MockMvc mockMvc;

    private Room mockRoom;
    private User mockUser;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        sessionService.addNewUser("test-user","123","test-user");
        mockUser = sessionService.getUserByAccount("test-user","123");
        roomService.addNewRoom("testRoom");
        mockRoom = roomMapper.getRoomByName("testRoom");
        session.setAttribute(Constants.ONLINE_USER,mockUser);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getRoomList() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/room/list/"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void enterRoom() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/room/enter/")
                .param("roomId", mockRoom.getId().toString()).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void enterRoom_user_not_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/room/enter/")
                .param("roomId", mockRoom.getId().toString()));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void enterRoom_roomId_null() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/room/enter/")
                .param("roomId", ""));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void getRoomByIdTest() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/room/detail/")
                .param("roomId",mockRoom.getId().toString()));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void deleteRoomByIdTest() throws Exception {
        int count = roomService.getRoomList().size();
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/room/" + mockRoom.getId().toString() + "/"));
        int newCount = roomService.getRoomList().size();
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
        AssertJUnit.assertEquals(count-1,newCount);
    }

    @Test
    public void addNewRoomTest() throws Exception {
        int count = roomService.getRoomList().size();
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/room/")
                .param("name","addNewRoomTest"));
        int newCount = roomService.getRoomList().size();
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
        AssertJUnit.assertEquals(count + 1,newCount);
    }

    @Test
    public void modifyRoomNameTest() throws Exception {
        String oldName = mockRoom.getRoomName();
        String newName = "modifyRoomNameTest";
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/room/modify/")
                .param("roomId",mockRoom.getId().toString())
                .param("name",newName));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
        AssertJUnit.assertEquals(roomService.getRoomById(mockRoom.getId()).getRoomName(),newName);
    }

    @Test
    public void exitRoomTest_no_login()throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/room/exit/"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void exitRoomTest() throws Exception {
        roomService.enterRoom(mockRoom.getId(),mockUser.getId());
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/room/exit/")
                .session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }
}