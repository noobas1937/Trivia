package com.ecnu.trivia.web.rbac.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.domain.vo.UserAccountVO;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
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
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class UserControllerTest {
    @Resource
    private UserController userController;
    @Resource
    private RoomService roomService;
    @Resource
    private UserMapper userMapper;

    private MockMvc mockMvc;
    @Resource
    private MockHttpSession session;

    private User mockUser;

    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("alucardtest","12345678","xiaotest",null);
        mockUser = userMapper.getUserByAccount("alucardtest","12345678");
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        session.setAttribute(Constants.ONLINE_USER,mockUser);
    }

    @Test
    public void get_current_user() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/user/currentUser/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
        // 也可以从response里面取状态码，header,cookies...
        // System.out.println(mvcResult.getResponse().getStatus());
    }

    @Test
    public void get_current_user_not_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/user/currentUser/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void  get_user_list() throws Exception {
        mockUser.setUserType(0);
        session.setAttribute(Constants.ONLINE_USER,mockUser);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/user/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("pno","1").param("PAGE_SIZE","5"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void  get_user_list_not_right_user() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/user/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("pno","1").param("PAGE_SIZE","5"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NO_JURISDICTION.getCode(),resp.getResCode());
    }

    @Test
    public void  add_new_user() throws Exception {
        User mockAddUser = new User();
        mockAddUser.setAccount("mockAddUserTestAccount");
        mockAddUser.setPassword("12345678");
        mockAddUser.setNickName("mockAddUserTestNickName");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(mockAddUser)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void  add_new_user_exist_same_account() throws Exception {
        userMapper.addNewUser("mockAddUserTestAccount","12345678","mockAddUserTestNickName",null);
        User mockAddUser = new User();
        mockAddUser.setAccount("mockAddUserTestAccount");
        mockAddUser.setPassword("12345678");
        mockAddUser.setNickName("mockAddUserTestNickName");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(mockAddUser)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),resp.getResCode());
    }

    @Test
    public void  delete_user() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.delete("/user/"+mockUser.getId()+"/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void  delete_user_exist_player() throws Exception {
        roomService.enterRoom(10,mockUser.getId());
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.delete("/user/"+mockUser.getId()+"/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),resp.getResCode());
    }

    @Test
    public void  modify_user() throws Exception {
        mockUser.setNickName("xiaotest2");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/user/modify/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(mockUser)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void  modify_user_no_id() throws Exception {
        mockUser.setNickName("xiaotest2");
        mockUser.setId(null);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/user/modify/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(mockUser)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void get_user_in_game_list() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/user/list/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }
}