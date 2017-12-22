package com.ecnu.trivia.web.game.service;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.ConstantsMsg;
import com.ecnu.trivia.web.utils.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 游戏逻辑服务，主要关注游戏过程中的相关操作
 * @author Jack Chen
 */
@Service
public class GameService implements Logable {
    private static Logger logger = LoggerFactory.getLogger(GameService.class);

    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private MessageService messageService;

    public boolean rollDice(Integer userId){
        //判断玩家是否合法 && 游戏是否为待掷骰子状态
        Player player = playerMapper.getPlayerByUserId(userId);
        if(player == null) { return false; }
        Game game = gameMapper.getGameByRoomId(player.getRoomId());
        if(!game.getStage().equals(Constants.GAME_READY)){ return false; }
        //判断当前玩家是不是该玩家
        Integer curPlayerID = game.getCurrentPlayerId();
        if(curPlayerID == null || !Objects.equals(curPlayerID, player.getId())) {
            return false;
        }

        //开始掷骰子
        Random random = new Random();
        Integer diceNumber = (random.nextInt(6)) + 1;
        gameMapper.updateGameStatus(game.getId(),player.getId(),diceNumber,game.getQuestionId(),Constants.GAME_DICE_RESULT);
        //发送掷骰子结果
        messageService.refreshUI(player.getRoomId());

        //判断玩家状态（能否答题）
        if(player.getStatus().equals(Constants.PLAYER_GAMING_HOLD)
                && (diceNumber%2) == 1){
            //在监狱中而且不能脱困
            Integer curPlayerId = player.getId();
            List<Player> players = playerMapper.getPlayers(curPlayerId);
            Integer nextPlayer = null;
            for (int i = 0; i < players.size(); i++) {
                if(Objects.equals(players.get(i).getId(), curPlayerId)){
                    nextPlayer = ++i%players.size();
                }
            }
            if(nextPlayer==null){
                logger.error(ConstantsMsg.ROOM_STATE_ERROR);
                return false;
            }
            //转向下一个玩家
            gameMapper.updateGameStatus(game.getId(),nextPlayer,diceNumber,game.getQuestionId(),Constants.GAME_READY);
            messageService.refreshUI(player.getRoomId());
        }
        else{
            //玩家前进 && 当前玩家选择答题类型
            playerMapper.updatePlayer(curPlayerID,player.getBalance()+1,
                    player.getPosition()+game.getDiceNumber(),Constants.PLAYER_GAMING_FREE);
            gameMapper.updateGameStatus(game.getId(),player.getId(),diceNumber,game.getQuestionId(),Constants.GAME_CHOOSE_TYPE);
            messageService.refreshUI(player.getRoomId());
        }
        return true;
    }

    /**
     * 判断当前房间是否所有用户都已准备
     * @author: Lucto Zhang
     * @date: 20:39 2017/12/18
     */
    public Resp checkReady(Integer userId,Integer ready) {
        RoomVO room = getRoomByUserId(userId);
        if (room.getStatus() != Constants.ROOM_WAITING) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //将当前用户的准备状态设为相应的状态
        playerMapper.setupUserState(userId,ready);
        messageService.refreshUI(room.getId());
        //如果当前用户取消准备，操作结束
        if (ready != Constants.PLAYER_READY) {
            return new Resp(HttpRespCode.SUCCESS);
        }

        List<Player> notReadyPlayers = playerMapper.getNotReadyPlayer(room.getId());
        //判断当前房间中所有玩家是否都已准备好
        if(ObjectUtils.isNotNullOrEmpty(notReadyPlayers)||notReadyPlayers.size()!=0) {
            return new Resp(HttpRespCode.SUCCESS);
        }
        //准备开始游戏
        Game game = gameMapper.getGameByRoomId((room.getId()));
        Integer currentPlayerId = room.getPlayerList().get(0).getId();
        if(!ObjectUtils.isNullOrEmpty(game)){
            gameMapper.updateGameStatus(game.getId(),currentPlayerId,-1,-1,Constants.GAME_READY);
        }else{
            gameMapper.addGame(room.getId(),currentPlayerId);
        }
        roomMapper.updateRoomStatus(room.getId(),Constants.ROOM_PLAYING);
        messageService.refreshUI(room.getId());
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
     * 判断房间是否已开始游戏
     * @author: Lucto Zhang
     * @date: 20:57 2017/12/20
     */
    public RoomVO getRoomByUserId(Integer userId){
        Player player = playerMapper.getPlayerByUserId(userId);
        return roomMapper.getRoomById(player.getRoomId());
    }

    /**
     * 根据userId刷新用户房间
     * @param userId
     * @author Jack Chen
     */
    public void refreshUserRoom(Integer userId) {
        RoomVO room = getRoomByUserId(userId);
        messageService.refreshUI(room.getId());
    }
}
