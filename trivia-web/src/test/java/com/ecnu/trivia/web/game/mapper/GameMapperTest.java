package com.ecnu.trivia.web.game.mapper;

import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import org.junit.Assert;
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
 * @author miss guo
 * @date 2018.1.4
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class GameMapperTest {
    @Resource
    private GameMapper gameMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private UserMapper userMapper;
    private User mockUser;
    private Room mockRoom;
    private Game mockGame;
    private Player mockPlayer;
    private Question mockQuestion;

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
    }

    @Test
    public void get_game_by_room_id() throws Exception {
        gameMapper.addGame(mockRoom.getId(),mockPlayer.getId());
        AssertJUnit.assertNotNull(gameMapper.getGameByRoomId(mockRoom.getId()));
    }

    @Test
    public void update_game_status() throws Exception {
        gameMapper.addGame(mockRoom.getId(),mockPlayer.getId());
        mockGame = gameMapper.getGameByRoomId(mockRoom.getId());
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),1,mockQuestion.getId(), Constants.GAME_READY);
        Game game = gameMapper.getGameByRoomId(mockRoom.getId());
        AssertJUnit.assertEquals(game.getCurrentPlayerId(),mockPlayer.getId());
        AssertJUnit.assertEquals((int)game.getDiceNumber(),1);
        AssertJUnit.assertEquals(game.getQuestionId(),mockQuestion.getId());
        AssertJUnit.assertEquals((int)game.getStage(),Constants.GAME_READY);
    }

    @Test
    public void get_game_by_question_id() throws Exception {
        List<Game> res=gameMapper.getGameByQuestionId(mockQuestion.getId());
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void add_game() throws Exception {
        gameMapper.addGame(mockRoom.getId(),mockPlayer.getId());
        Game res=gameMapper.getGameByRoomId(mockRoom.getId());
        AssertJUnit.assertNotNull(res);
    }

    @Test
    public void get_appropriate_ready_room_id() throws Exception {
        gameMapper.getAppropriateReadyRoomId(4);
    }

}