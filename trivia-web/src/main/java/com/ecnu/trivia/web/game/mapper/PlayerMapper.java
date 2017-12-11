package com.ecnu.trivia.web.game.mapper;

import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.web.game.domain.Player;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerMapper extends Mapper<Player> {
    /**用户准备/取消准备*/
    void isReady(@Param("userID")Integer userID,@Param("isReady")Integer isReady);

    /**向房间添加玩家*/
    void addPlayer(@Param("roomID")Integer roomID, @Param("userID")Integer userID);

    /**向房间删除玩家*/
    void removePlayer(@Param("userID")Integer userID);

    /**获取房间中玩家的数量*/
    Integer getPlayerCount(@Param("roomID")Integer roomID);

    /**根据user_id获取player_id*/
    Integer getPlayerIdByUserId(@Param("userID")Integer userID);

    /**根据player_id获取其所在房间的current_player_id*/
    Integer getRoomCurrentPlayerIdByPlayerId(@Param("playerID")Integer playerID);

    /**根据player_id获取其状态*/
    Integer getPlayerStatusByPlayerId(@Param("playerID")Integer playerID);

    /**获取问题的总数量*/
    Integer getQuestionCount();
}
