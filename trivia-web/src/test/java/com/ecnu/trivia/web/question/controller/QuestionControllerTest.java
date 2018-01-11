package com.ecnu.trivia.web.question.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
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
    private GameMapper gameMapper;
    @Resource
    private RoomService roomService;
    @Resource
    private PlayerMapper playerMapper;

    private MockMvc mockMvc;
    private User mockUser;
    private Player mockPlayer;
    private Game mockGame;
    private Question mockQuestion;

    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("test-user","123","test-user",null);
        mockUser = userMapper.getUserByAccount("test-user","123");
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
        questionMapper.addQuestionWithId(-1000,"test","A","B",
                "C","D",1,1);
        roomService.enterRoom(10,mockUser.getId());
        mockQuestion = questionMapper.getQuestionById(-1000);
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
    public void delete_question_without_id() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/question/")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
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
    public void modifyQuestion() throws Exception {

    }

    @Test
    public void getQuestionTypeList() throws Exception {

    }

    @Test
    public void getQuestionTypeList1() throws Exception {

    }

    @Test
    public void addQuestionType() throws Exception {

    }

    @Test
    public void modifyQuestionTypeName() throws Exception {

    }

    @Test
    public void getAllQuestion() throws Exception {

    }

}