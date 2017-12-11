package com.ecnu.trivia.web.game.mapper;

import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.web.room.domain.Player;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerMapper extends Mapper<Player> {
    void isReady(@Param("userId")Integer userId,@Param("isReady")Integer isReady);
}
