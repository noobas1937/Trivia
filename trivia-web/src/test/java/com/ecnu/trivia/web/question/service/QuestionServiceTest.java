package com.ecnu.trivia.web.question.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.game.service.GameService;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class QuestionServiceTest {
    private QuestionVO mockQuestion;
    private User mockUser;
    private User mockUser1;
    private Player mockPlayer;
    private Player mockPlayer1;
    private Game mockGame;

    @Resource
    private SessionService sessionService;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private RoomService roomService;
    @Resource
    private PlayerMapper playerMapper;

    @Before
    public void setUp() throws Exception {
        mockQuestion = questionMapper.getQuestions(1,10).get(0);
        sessionService.addNewUser("test-user","123","test-user");
        sessionService.addNewUser("test-user1","123","test-user1");
        mockUser = sessionService.getUserByAccount("test-user","123");
        mockUser1 = sessionService.getUserByAccount("test-user1","123");
        roomService.enterRoom(10,mockUser.getId());
        mockPlayer = playerMapper.getPlayerByUserId(mockUser.getId());
        mockGame = gameMapper.getGameByRoomId(10);
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),6,
                mockQuestion.getId(),Constants.GAME_ANSWERING_QUESTION);
    }
    @Test
    public void add_question() throws Exception {
        Integer before = questionMapper.getQuestionsCount();
        questionService.addQuestion("xixixi","1","2","3","4",3,1);
        Integer after = questionMapper.getQuestionsCount();
        AssertJUnit.assertEquals(1,after-before);
    }

    @Test
    public void get_game_by_question_id() throws Exception {
        List<Game> successRes=questionService.getGameByQuestionId(mockQuestion.getId());
        AssertJUnit.assertNotNull(successRes);
    }

    @Test
    public void delete_question() throws Exception {
        questionService.deleteQuestion(mockQuestion.getId());
        Question question=questionMapper.getQuestionById(mockQuestion.getId());
        AssertJUnit.assertNull(question);
    }

    @Test
    public void modify_question_which_is_exist_and_no_info() throws Exception {
        Question last= (Question)questionService.getQuestionById(mockUser.getId(),mockQuestion.getId()).getData();
        Question mockQuestionParam = new Question(mockQuestion.getId(),null,null,null,null,null,null,null,null);
        questionService.modifyQuestion(mockQuestionParam);
        Question now= (Question)questionService.getQuestionById(mockUser.getId(),mockQuestion.getId()).getData();
        AssertJUnit.assertEquals(last.getAnswer(),now.getAnswer());
    }

    @Test
    public void modify_question_which_is_exist_and_modify_all_info() throws Exception {
        Question last= (Question)questionService.getQuestionById(mockUser.getId(),mockQuestion.getId()).getData();
        Question mockQuestionParam = new Question(mockQuestion.getId(),"23",mockQuestion.getTypeId(),"A","A","A","A",1,mockQuestion.getStatus());
        questionService.modifyQuestion(mockQuestionParam);
        Question now= (Question)questionService.getQuestionById(mockUser.getId(),mockQuestion.getId()).getData();
        AssertJUnit.assertEquals(last.getAnswer(),now.getAnswer());
    }

    @Test
    public void modify_question_which_is_not_exist() throws Exception {
        Question mockQuestionParam = new Question(-1000,null,null,null,null,null,null,null,null);
        questionService.modifyQuestion(mockQuestionParam);
    }

    @Test
    public void getQuestionById_player_is_null() throws Exception {
        Resp successRes=questionService.getQuestionById(mockUser.getId(),mockQuestion.getId());
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),successRes.getResCode());
    }
    @Test
    public void getQuestionById_game_is_not_ready_for_answer() throws Exception {
        Resp successRes=questionService.getQuestionById(mockUser.getId(),mockQuestion.getId());
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),successRes.getResCode());
    }
    @Test
    public void getQuestionById() throws Exception{
        Resp successRes=questionService.getQuestionById(mockUser.getId(),mockQuestion.getId());
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),successRes.getResCode());
    }
    @Test
    public void checkQuestionAnswer_player_is_null() throws Exception {
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getId());
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),successRes.getResCode());
    }
    @Test
    public void checkQuestionAnswer_game_is_not_ready_for_answer() throws Exception {
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getId());
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),successRes.getResCode());
    }

    @Test
    public void checkQuestionAnswer_player_is_not_the_user() throws Exception{
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getAnswer());
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),successRes.getResCode());
    }
    @Test
    public void checkQuestionAnswer_player_is_not_exist() throws Exception{
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getAnswer());
        AssertJUnit.assertEquals(HttpRespCode.METHOD_NOT_ALLOWED.getCode(),successRes.getResCode());
    }
    @Test
    public void checkQuestionAnswer_answer_is_right() throws Exception{
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getAnswer());
        AssertJUnit.assertEquals(true,successRes.getData());
    }
    @Test
    public void checkQuestionAnswer_answer_is_wrong() throws Exception{
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getAnswer()+1);
        AssertJUnit.assertEquals(false,successRes.getData());
    }
    @Test
    public void checkQuestionAnswer_game_is_over() throws Exception{
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getAnswer());
        AssertJUnit.assertEquals(true,successRes.getData());
    }
    @Test
    public void checkQuestionAnswer_game_is_not_over() throws Exception{
        Resp successRes=questionService.checkQuestionAnswer(mockUser.getId(),mockQuestion.getAnswer());
        AssertJUnit.assertEquals(false,successRes.getData());
    }


    @Test
    public void getAllQuestions() throws Exception {
        List successRes=questionService.getAllQuestions(1,10);
        AssertJUnit.assertNotNull(successRes);
    }

    @Test
    public void getAllQuestionsCount_the_player_is_elegal() throws Exception {

    }

    @Test
    public void generateRandomQuestion() throws Exception {

    }

}