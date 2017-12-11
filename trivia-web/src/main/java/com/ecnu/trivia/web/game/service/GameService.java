package com.ecnu.trivia.web.game.service;

import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("gameService")
public class GameService implements Logable {
    private static Logger logger = LoggerFactory.getLogger(GameService.class);

    @Resource
    private PlayerMapper playerMapper;

    public void isReady(int userId,int isReady){
        playerMapper.isReady(userId,isReady);
    }
}
