package com.ecnu.trivia.web.game.service;

import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.vo.PlayerVO;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
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
    private SessionService sessionService;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private RoomService roomService;
    private User mockUser;

    @Before
    public void setUp() throws Exception {
        //模拟用户
        sessionService.addNewUser("test-user","123","test-user");
        mockUser = sessionService.getUserByAccount("test-user","123");
        //将玩家添加到10号房间
        roomMapper.updateRoomStatus(10, Constants.ROOM_WAITING);
        roomService.enterRoom(10,mockUser.getId());
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
    public void rollDice() throws Exception {
        boolean res = gameService.rollDice(2);
        AssertJUnit.assertEquals(false,res);
    }

    @Test
    public void checkReady() throws Exception {
        gameService.checkReady(2,1);
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