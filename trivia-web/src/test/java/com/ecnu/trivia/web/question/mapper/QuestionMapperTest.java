package com.ecnu.trivia.web.question.mapper;

import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author Jack Chen
 * @date 2018/1/4
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class QuestionMapperTest {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private QuestionTypeMapper questionTypeMapper;
    private User mockUser;
    private Room mockRoom;
    private Game mockGame;
    private Player mockPlayer;
    private Question mockQuestion;
    private QuestionType mockQuestionType;
    @Before
    public void setUp() throws Exception {
        userMapper.addNewUser("test-user","12345678","nickName",null);
        mockUser = userMapper.getUserByAccount("test-user","12345678");
        roomMapper.addRoomByName("test-room");
        mockRoom = roomMapper.getRoomByName("test-room");
        playerMapper.addPlayer(mockRoom.getId(),mockUser.getId());
        mockPlayer =playerMapper.getPlayerByUserId(mockUser.getId());
        questionMapper.addQuestionWithId(55555,"Test Question","a","b","c","d",1,1);
        mockQuestion = questionMapper.getQuestionById(55555);
        gameMapper.addGame(mockRoom.getId(),mockPlayer.getId());
        mockGame = gameMapper.getGameByRoomId(mockRoom.getId());
        questionTypeMapper.addQuestionType("test-question-type");
        mockQuestionType = questionTypeMapper.getQuestionTypeByDesc("test-question-type").get(0);

    }

    @Test
    public void delete_question() throws Exception {
        questionMapper.deleteQuestion(mockQuestion.getId());
        Question question = questionMapper.getQuestionById(mockQuestion.getId());
        if(ObjectUtils.isNotNullOrEmpty(question)){
            AssertJUnit.fail("删除问题："+mockQuestion.getId()+" 失败！");
        }
    }

    @Test
    public void get_question_by_id() throws Exception {
        Question question = questionMapper.getQuestionById(mockQuestion.getId());
        if(ObjectUtils.isNullOrEmpty(question)){
            AssertJUnit.fail("获取问题："+mockQuestion.getId()+" 失败！");
        }
    }

    @Test
    public void get_question_by_user_id() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),1,mockQuestion.getId(), Constants.GAME_READY);
        Question question = questionMapper.getQuestionByUserId(mockUser.getId());
        AssertJUnit.assertNotNull(question);
        Question questionEmpty = questionMapper.getQuestionByUserId(-1000);
        AssertJUnit.assertNull(questionEmpty);
    }

    @Test
    public void modify_question() throws Exception {
        Question modifyQuestion = new Question(mockQuestion.getId(),"test",1,"A","B",
                "C","D",1,1);
        questionMapper.modifyQuestion(modifyQuestion);
        Question question = questionMapper.getQuestionById((mockQuestion.getId()));
        AssertJUnit.assertNotNull(question);
        AssertJUnit.assertEquals("test",question.getDescription());
    }

    @Test
    public void add_question() throws Exception {
        Integer countInit = questionMapper.getQuestionsCount();
        questionMapper.addQuestion("test","A","B",
                "C","D",1,1);
        Integer countEnd = questionMapper.getQuestionsCount();
        AssertJUnit.assertEquals(1,countEnd-countInit);
    }

    @Test
    public void get_questions_by_question_type_id() throws Exception {
        List<Question> questions = questionMapper.getQuestionsByQuestionTypeId(mockQuestionType.getId());
        AssertJUnit.assertNotNull(questions);
    }

    @Test
    public void get_questions() throws Exception {
        List<QuestionVO> questions = questionMapper.getQuestions(1,10);
        AssertJUnit.assertNotNull(questions);
    }

    @Test
    public void get_questions_count() throws Exception {
        Integer questionCount = questionMapper.getQuestionsCount();
        AssertJUnit.assertNotNull(questionCount);
        if(questionCount<0){
            AssertJUnit.fail("问题数量为负数！");
        }
    }

    @Test
    public void generate_random_question() throws Exception {
        Integer questionIdEmpty = questionMapper.generateRandomQuestion(-1000);
        AssertJUnit.assertNull(questionIdEmpty);
        QuestionType type = questionTypeMapper.getQuestionTypes().get(0);
        Integer questionId = questionMapper.generateRandomQuestion(type.getId());
        AssertJUnit.assertNotNull(questionId);
        Question question = questionMapper.getQuestionById(questionId);
        AssertJUnit.assertNotNull(question);
    }

}