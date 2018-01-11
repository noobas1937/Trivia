package com.ecnu.trivia.web.question.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.question.mapper.QuestionTypeMapper;
import com.ecnu.trivia.web.rbac.controller.SessionController;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
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
public class QuestionControllerTest {

    @Resource
    private QuestionController questionController;
    @Resource
    private MockHttpSession session;
    @Resource
    private UserMapper userMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionTypeMapper questionTypeMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private RoomService roomService;
    @Resource
    private PlayerMapper playerMapper;

    private User mockUser;
    private Game mockGame;
    private MockMvc mockMvc;
    private Player mockPlayer;
    private Question mockQuestion;
    private QuestionType mockQuestionType;

    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("test-user","123","test-user",null);
        mockUser = userMapper.getUserByAccount("test-user","123");
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
        questionMapper.addQuestionWithId(-1000,"test","A","B",
                "C","D",1,1);
        roomService.enterRoom(10,mockUser.getId());
        mockQuestion = questionMapper.getQuestionById(-1000);
        questionTypeMapper.addQuestionType("test-question-type");
        mockQuestionType = questionTypeMapper.getQuestionTypeByDesc("test-question-type").get(0);
        mockPlayer = playerMapper.getPlayerByUserId(mockUser.getId());
        mockGame = gameMapper.getGameByRoomId(10);
    }

    @Test
    public void add_question_with_all_information() throws Exception {
        mockQuestion.setDescription("test:" + mockQuestion.getDescription());
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/question/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(JSON.toJSONString(mockQuestion))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .session(session)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void delete_question_with_valid_question_id() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/question/"+mockQuestion.getId()+"/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }


    @Test
    public void delete_question_with_question_in_use() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),6,
                mockQuestion.getId(), Constants.GAME_ANSWERING_QUESTION);
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders
                .delete("/question/"+mockQuestion.getId()+"/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.QUESTION_ARE_USED.getCode(),resp.getResCode());
    }

    @Test
    public void modify_question() throws Exception {
        mockQuestion.setDescription("test-modify");
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/question/modify/")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(JSON.toJSONString(mockQuestion))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void modify_question_without_id() throws Exception {
        mockQuestion.setId(null);
        ResultActions resultActions = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post("/question/modify/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(JSON.toJSONString(mockQuestion))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void get_question_types() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
            MockMvcRequestBuilders
                .get("/question/type/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("pno","1")
                .param("PAGE_SIZE","10")
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void delete_question_type() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/question/type/"+mockQuestionType.getId()+"/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void add_question_with_some_empty_information() throws Exception {
        mockQuestion.setDescription(null);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/question/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(JSON.toJSONString(mockQuestion))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .session(session)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void add_question_type() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post("/question/type/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("description","test-add-question-type")
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void modify_question_type_name() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/question/type/modify/")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .param("questionId",mockQuestion.getId()+"")
                        .param("description","test-modify-question-type")
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void get_questions() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(
            MockMvcRequestBuilders
                .get("/question/retrive/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("pno","1")
                .param("PAGE_SIZE","10")
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

}