package com.ecnu.trivia.web.message.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.utils.Resp;
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

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class MessageControllerTest {

    @Resource
    private MessageController messageController;
    @Resource
    private MockHttpSession session;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    public void sendToAllTerminal() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/message/all/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("message","testmsg").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

}