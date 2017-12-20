package com.ecnu.trivia.web.game.service;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.ConstantsMsg;
import gherkin.lexer.Pl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

    /**
     * 用户准备（取消准备）
     * @author: Lucto Zhang
     */
    public void isReady(int userId,int isReady){
        playerMapper.isReady(userId,isReady);
    }

    public boolean rollDice(Integer userId){
        //判断玩家是否合法 && 游戏是否为待掷骰子状态
        Player player = playerMapper.getPlayerByUserId(userId);
        if(player == null) { return false; }
        Game game = gameMapper.getGameById(player.getRoomId());
        if(!game.getStage().equals(Constants.GAME_READY)){ return false; }
        //判断当前玩家是不是该玩家
        Integer curPlayerID = game.getCurrentPlayerId();
        if(curPlayerID == null || !Objects.equals(curPlayerID, player.getId())) {
            return false;
        }

        //开始掷骰子 && 生成随机题目
        Integer questionCount = playerMapper.getQuestionCount();
        Random random = new Random();
        Integer questionOrder = (random.nextInt(questionCount));
        Integer diceNumber = (random.nextInt(6)) + 1;
        gameMapper.updateGameStatus(game.getId(),player.getId(),diceNumber,questionOrder,Constants.GAME_DICE_RESULT);
        //发送掷骰子结果
        messageService.refreshUI(player.getRoomId());

        //判断玩家状态（能否答题）
        if(player.getStatus().equals(Constants.PLAYER_GAMING_HOLD)
                && (diceNumber%2) == 1){
            //在监狱中而且不能脱困,此时stage = GAME_READY ,current_player = next_player
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
            gameMapper.updateGameStatus(game.getId(),nextPlayer,diceNumber,questionOrder,Constants.GAME_READY);
            messageService.refreshUI(player.getRoomId());
        }
        else{
            //玩家前进 && 当前玩家答题操作
            playerMapper.updatePlayer(curPlayerID,player.getBalance()+1,
                    player.getPosition()+game.getDiceNumber(),Constants.PLAYER_GAMING_FREE);
            gameMapper.updateGameStatus(game.getId(),player.getId(),diceNumber,questionOrder,Constants.GAME_ANSWERING_QUESTION);
            messageService.refreshUI(player.getRoomId());
        }
        return true;
    }

    /**
     * 判断当前房间是否所有用户都已准备
     * @author: Lucto Zhang
     * @Date: 20:39 2017/12/18
     */
    public void isAllReady(Integer userId) {
        Player player = playerMapper.getPlayerByUserId(userId);
        if (!ObjectUtils.isNullOrEmpty(player)) {
            List<Player> notReadyPlayers = playerMapper.getNotReadyPlayer(player.getRoomId());
            //判断当前房间中所有玩家是否都已准备好
            if(ObjectUtils.isNullOrEmpty(notReadyPlayers)){
                //准备开始游戏
                Game game = gameMapper.getGameById(player.getRoomId());
                if(!ObjectUtils.isNullOrEmpty(game)){
                    Integer currentPlayerId = playerMapper.getPlayers(player.getId()).get(0).getId();
                    gameMapper.updateGameStatus(game.getId(),currentPlayerId,-1,-1,Constants.GAME_READY);
                }else{
                    Integer currentPlayerId = playerMapper.getPlayers(player.getId()).get(0).getId();
                    gameMapper.addGame(player.getRoomId(),currentPlayerId);
                }
            }
        }
    }

    /**
     * 判断房间是否已开始游戏
     * @author: Lucto Zhang
     * @Date: 20:57 2017/12/20
     */
    public boolean roomWaiting(Integer userId){
        Player player = playerMapper.getPlayerByUserId(userId);
        RoomVO room = roomMapper.getRoomById(player.getRoomId());
        if(room.getStatus()==Constants.ROOM_WAITING) {
            return true;
        }else{
            return false;
        }
    }
}
