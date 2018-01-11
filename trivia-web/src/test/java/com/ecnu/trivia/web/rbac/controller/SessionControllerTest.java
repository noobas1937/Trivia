package com.ecnu.trivia.web.rbac.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.domain.vo.UserAccountVO;
import com.ecnu.trivia.web.rbac.domain.vo.UserRegisterVO;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
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

import java.io.File;
import java.io.FileInputStream;

import static org.testng.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class SessionControllerTest {
    @Resource
    private SessionController sessionController;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MockHttpSession session;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private MockMvc mockMvc;

    private User mockUser;

    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("alucardtest","12345678","xiaotest",null);
        mockUser = userMapper.getUserByAccount("alucardtest","12345678");
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
        session.setAttribute(Constants.ONLINE_USER,mockUser);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loginTest() throws Exception {
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setAccount(mockUser.getAccount());
        userAccountVO.setPassword("12345678");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/session/login/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(userAccountVO)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void loginTest_no_account() throws Exception {
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setAccount(null);
        userAccountVO.setPassword("12345678");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/session/login/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(userAccountVO)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void loginTest_no_match_user() throws Exception {
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setAccount(mockUser.getAccount());
        userAccountVO.setPassword("123456789");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/session/login/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .content(JSON.toJSONString(userAccountVO)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_PASS_NOT_MATCH.getCode(),resp.getResCode());
    }

    @Test
    public void logoutTest() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/session/logout/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void registerTest() throws Exception {
        UserRegisterVO userRegisterVO = new UserRegisterVO();
        userRegisterVO.setAccount("mockRegisterAccount");
        userRegisterVO.setHeadpic("mockRegisterHeadPic");
        userRegisterVO.setNickname("mockRegisterNickName");
        userRegisterVO.setPassword("12345678");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/session/register/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(JSON.toJSONString(userRegisterVO)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void registerTest_info_not_complete() throws Exception {
        UserRegisterVO userRegisterVO = new UserRegisterVO();
        userRegisterVO.setAccount("mockRegisterAccount");
        userRegisterVO.setHeadpic("mockRegisterHeadPic");
        userRegisterVO.setNickname("mockRegisterNickName");
        userRegisterVO.setPassword(null);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/session/register/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(JSON.toJSONString(userRegisterVO)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void registerTest_already_exists() throws Exception {
        UserRegisterVO userRegisterVO = new UserRegisterVO();
        userRegisterVO.setAccount("alucardtest");
        userRegisterVO.setHeadpic("mockRegisterHeadPic");
        userRegisterVO.setNickname("mockRegisterNickName");
        userRegisterVO.setPassword("12345678");
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/session/register/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(JSON.toJSONString(userRegisterVO)).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_ACCOUNT_EXISTS.getCode(),resp.getResCode());
    }

    @Test
    public void getUserInfoTest() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/session/current/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        User user = JSON.parseObject(resp.getData().toString(), new TypeReference<User>() {});
        AssertJUnit.assertNotSame(user,null);
    }

    @Test
    public void getUserInfoTest_wrong() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/session/current/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        User user = JSON.parseObject(resp.getData().toString(), new TypeReference<User>() {});
        AssertJUnit.assertEquals(user.getAccount(),null);
    }

    @Test
    public void uploadHeadPicTest() throws Exception {
        File output = temporaryFolder.newFile("output.png");
        FileInputStream fis = new FileInputStream(output);
        MockMultipartFile mfile = new MockMultipartFile("C:/Users/sei_z/Desktop", "output.png", "application/vnd_ms-excel", fis);
        Resp resp = sessionController.register(mfile);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }



}