package com.ecnu.trivia.web.game.mapper;

import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.room.domain.Room;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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

    /**获取房间中的玩家*/
    List<Player> getPlayers(@Param("id")Integer lastPlayerId);

    /**根据user_id获取player**/
    Player getPlayerByUserId(@Param("userID")Integer userID);

    /**获取问题的总数量*/
    Integer getQuestionCount();


}
