package com.ecnu.trivia.web.game.mapper;

import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.question.domain.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMapper extends Mapper<Player> {
    /**根据房间ID获取游戏状态*/
    Game getGameById(@Param("id")Integer roomId);
    /**更新 游戏信息*/
    void updateGameStatus(@Param("id")Integer gameId,
                          @Param("pid")Integer curPlayerId,
                          @Param("diceNum")Integer diceNumber,
                          @Param("qid")Integer questionId,
                          @Param("stage")Integer stage);

    /**根据问题ID获取游戏**/
    Game getGameByQuestionId(@Param("id") Integer id);
}
