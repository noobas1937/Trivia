package com.ecnu.trivia.web.game.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.domain.vo.PlayerVO;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.question.service.QuestionService;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import com.sun.tools.internal.jxc.ap.Const;
import org.junit.After;
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
public class GameServiceTest {

    @Resource
    private GameService gameService;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private SessionService sessionService;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private RoomService roomService;
    @Resource
    private QuestionMapper questionMapper;
    private User mockUser;
    private User mockUser1;
    private User mockUser2;
    private Game mockGame;
    private Player mockPlayer;
    private Player mockPlayer1;
    private QuestionVO mockQuestion;

    @Before
    public void setUp() throws Exception {
        //模拟用户
        sessionService.addNewUser("test-user","123","test-user");
        sessionService.addNewUser("test-user1","123","test-user1");
        sessionService.addNewUser("test-user2","123","test-user2");
        mockUser = sessionService.getUserByAccount("test-user","123");
        mockUser1 = sessionService.getUserByAccount("test-user1","123");
        mockUser2 = sessionService.getUserByAccount("test-user2","123");
        //将玩家添加到10号房间
        roomMapper.updateRoomStatus(10, Constants.ROOM_WAITING);
        roomService.enterRoom(10,mockUser.getId());
        roomService.enterRoom(10,mockUser1.getId());
        mockGame = gameMapper.getGameByRoomId(10);
        mockPlayer = playerMapper.getPlayerByUserId(mockUser.getId());
        mockPlayer1 = playerMapper.getPlayerByUserId(mockUser1.getId());
        mockQuestion = questionMapper.getQuestions(1,10).get(0);
    }

    @Test
    public void getRoomByUserId() throws Exception {
        RoomVO roomVO = gameService.getRoomByUserId(mockUser.getId());
        List<PlayerVO> playerList = roomVO.getPlayerList();
        if(ObjectUtils.isNullOrEmpty(playerList)){
            AssertJUnit.fail();
        }
        for (PlayerVO playerVO: playerList) {
            if(playerVO.getUserId()==mockUser.getId()){
                return;
            }
        }
        AssertJUnit.fail();
    }


    @Test
    public void roll_dice_with_player_not_in_room() throws Exception {
        boolean res = gameService.rollDice(mockUser2.getId());
        AssertJUnit.assertEquals(false,res);
    }

    @Test
    public void roll_dice_with_illegal_player() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),-1,mockQuestion.getId(), Constants.GAME_READY);
        boolean res = gameService.rollDice(mockUser1.getId());
        AssertJUnit.assertEquals(false,res);
    }

    @Test
    public void roll_dice_with_illegal_game_status_and_legal_player() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer1.getId(),-1,mockQuestion.getId(), Constants.GAME_ANSWERING_QUESTION);
        boolean res = gameService.rollDice(mockUser1.getId());
        AssertJUnit.assertEquals(false,res);
    }

    @Test
    public void roll_dice_with_player_not_current() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),-1,mockQuestion.getId(), Constants.GAME_ANSWERING_QUESTION);
        boolean res = gameService.rollDice(mockUser1.getId());
        AssertJUnit.assertEquals(false,res);
    }

    @Test
    public void roll_dice_with_player_legal_and_can_answer_question() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),-1,mockQuestion.getId(), Constants.GAME_READY);
        playerMapper.updatePlayer(mockPlayer.getId(),mockPlayer.getBalance(),mockPlayer.getPosition(),Constants.PLAYER_GAMING_FREE);
        boolean res = gameService.rollDice(mockUser.getId());
        AssertJUnit.assertEquals(true,res);
    }

    @Test
    public void roll_dice_with_player_legal_and_can_not_answer_question() throws Exception {
        gameMapper.updateGameStatus(mockGame.getId(),mockPlayer.getId(),-1,mockQuestion.getId(), Constants.GAME_READY);
        playerMapper.updatePlayer(mockPlayer.getId(),mockPlayer.getBalance(),mockPlayer.getPosition(),Constants.PLAYER_GAMING_HOLD);
        boolean res = gameService.rollDice(mockUser.getId());
        AssertJUnit.assertEquals(true,res);
    }

    @Test
    public void check_ready_when_user_cancel_ready() throws Exception {
        Resp resp = gameService.checkReady(mockUser.getId(), Constants.PLAYER_WAITING);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void check_ready_when_other_user_not_ready() throws Exception {
        Resp resp = gameService.checkReady(mockUser.getId(), Constants.PLAYER_READY);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void check_ready_when_all_users_ready() throws Exception {
        mockPlayer1.setStatus(Constants.PLAYER_READY);
        playerMapper.updatePlayer(mockPlayer1.getId(),mockPlayer1.getBalance(),mockPlayer1.getPosition(),mockPlayer1.getStatus());
        Resp resp = gameService.checkReady(mockUser.getId(), Constants.PLAYER_READY);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),resp.getResCode());
    }

    @Test
    public void get_room_by_user_id() throws Exception{
        RoomVO room = gameService.getRoomByUserId(mockUser.getId());
        AssertJUnit.assertEquals(10,(int)room.getId());
    }

    @Test
    public void getAppropriateReadyRoomId() throws Exception {
        gameService.getAppropriateReadyRoomId();
    }

    @Test
    public void refresh_user_room_with_correct_user_id() throws Exception {
        gameService.refreshUserRoom(2);
    }

    @Test
    public void refresh_user_room_with_error_user_id() throws Exception {
        gameService.refreshUserRoom(-1000);
    }

}