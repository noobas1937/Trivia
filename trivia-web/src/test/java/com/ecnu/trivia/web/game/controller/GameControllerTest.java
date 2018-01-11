package com.ecnu.trivia.web.game.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
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

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class GameControllerTest {
    @Resource
    private GameController gameController;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private MockHttpSession session;

    private MockMvc mockMvc;

    private User mockUser;
    private Player mockPlayer;
    private Game mockGame;
    private Question mockQuestion;

    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("testUser","12345678","test",null);
        mockUser = userMapper.getUserByAccount("testUser","12345678");
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        session.setAttribute(Constants.ONLINE_USER,mockUser);
        playerMapper.addPlayer(20,mockUser.getId());
        mockPlayer = playerMapper.getPlayerByUserId(mockUser.getId());
        gameMapper.addGame(20,mockPlayer.getId());
        mockGame = gameMapper.getGameByRoomId(20);
        questionMapper.addQuestionWithId(55555,"Test Question","a","b","c","d",1,1);
        mockQuestion = questionMapper.getQuestionById(55555);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isReadyTest_success() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/ready/"+Constants.PLAYER_READY+"/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void isReadyTest_param_error() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/ready/"+Constants.PLAYER_GAMING_FREE+"/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void isReadyTest_user_not_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/ready/"+Constants.PLAYER_READY+"/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void refreshUI_success() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/refresh/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void refreshUI_user_not_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/refresh/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void rollDice_success() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/roll/dice/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void rollDice_user_not_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/roll/dice/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void qucikJoinGameRoom_success() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/game/qucikJoin/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void qucikJoinGameRoom_user_not_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/game/qucikJoin/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void getQuestionTypeList() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/question/type/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void getQuestionByQuestionType_success() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_CHOOSE_TYPE);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/question/choose/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("type","1"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void getQuestionByQuestionType_param_error() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_CHOOSE_TYPE);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/question/choose/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("type","1"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void getQuestionById_success() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_ANSWERING_QUESTION);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/question/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("id","55555"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void getQuestionById_param_error() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_ANSWERING_QUESTION);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/question/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("id","55555"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void checkQuestionAnswer_success() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_ANSWERING_QUESTION);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/game/question/answer/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("answer","2"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void checkQuestionAnswer_user_not_login() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_ANSWERING_QUESTION);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/game/question/answer/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("answer","2"));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.USER_NOT_LOGIN.getCode(),resp.getResCode());
    }

    @Test
    public void checkQuestionAnswer_param_error() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),3,55555,Constants.GAME_ANSWERING_QUESTION);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/game/question/answer/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session)
                .param("answer",""));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.PARAM_ERROR.getCode(),resp.getResCode());
    }

    @Test
    public void rollDice_result_false() throws Exception {
        playerMapper.removePlayer(mockUser.getId());
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/game/roll/dice/" )
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).session(session));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("=====客户端获得反馈数据:" + result);
        Resp resp = JSON.parseObject(result, new TypeReference<Resp>() {});
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),resp.getResCode());
    }
}