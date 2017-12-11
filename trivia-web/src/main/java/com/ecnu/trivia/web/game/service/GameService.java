package com.ecnu.trivia.web.game.service;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;

@Service
public class GameService implements Logable {
    private static Logger logger = LoggerFactory.getLogger(GameService.class);

    @Resource
    private PlayerMapper playerMapper;

    public void isReady(int userId,int isReady){
        playerMapper.isReady(userId,isReady);
    }

    public boolean rollDice(Integer userId){

        //检验该玩家掷骰子的行为是否合法
        Integer playerId = playerMapper.getPlayerIdByUserId(userId);
        if(playerId == null)
            return false;
        Integer currentPlayerId = playerMapper.getRoomCurrentPlayerIdByPlayerId(playerId);
        if(currentPlayerId == null || !currentPlayerId.equals(playerId))
            return false;

        Integer questionCount = playerMapper.getQuestionCount();
        Integer questionNumber = new Integer((new Random().nextInt(questionCount.intValue()))+1);
        Integer diceNumber = new Integer((new Random().nextInt(6))+1);
        //打包发送1 此时stage = GAME_DICE_RESULT ,current_player = player_id

        boolean isPlayerCanAnswerQuestion;
        if(playerMapper.getPlayerStatusByPlayerId(playerId).equals(new Integer(Constants.PLAYER_GAMING_HOLD))
                && (diceNumber%2) == 1){
            //打包发送2 在监狱中而且不能脱困,此时stage = GAME_READY ,current_player = next_player
        }
        else{
            //打包发送2 可以答题,此时stage = GAME_ANSWERING_QUESTION ,current_player = player_id
        }
        return true;
    }
}
