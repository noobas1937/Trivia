package com.ecnu.trivia.web.rbac.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.domain.vo.UserAccountVO;
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
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class UserControllerTest {
    @Resource
    private SessionController sessionController;
    @Resource
    private UserController userController;

    private MockMvc mockMvc;
    @Resource
    private MockHttpSession session;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        User user = new User();
        user.setId(2);
        user.setAccount("siyuan");
        user.setPassword("12345678");
        session.setAttribute(Constants.ONLINE_USER,user);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getCurrentUserTest() throws Exception {
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
}