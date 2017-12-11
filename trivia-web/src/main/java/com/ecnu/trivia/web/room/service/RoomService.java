/**
 * Service编写规范：
 *  1、所有Service类需要添加 @Service 注解标识
 *  2、所有Service类需要实现 Logable 接口
 *  3、所有Service在类内部使用 @Resource 自动注入 Mapper、Service
 *  4、如果有URL或配置常量请使用 @Value("${name}")自动装配
 *  5、所有 Service 类请添加 logger 日志器，并熟练使用logger
 *
 * @author Jack Chen
 */
package com.ecnu.trivia.web.room.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Service
public class RoomService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Resource
    private RoomMapper roomMapper;
    @Resource
    private PlayerMapper playerMapper;

    public List<RoomVO> getRoomList(){
        return roomMapper.getRoomList();
    }

    public RoomVO getRoomById(Integer id){
        return roomMapper.getRoomById(id);
    }

    public Resp editPlayerRoom(Integer roomId, Integer userID, Integer isEnter) {
        if(Objects.equals(isEnter, Constants.ROOM_ENTER)) {
            Integer integer = playerMapper.getPlayerCount(roomId);
            if (ObjectUtils.isNotNullOrEmpty(integer) && integer > Constants.MAX_PLAYER_COUNT) {
                return new Resp(HttpRespCode.ROOM_FULL);
            }
            playerMapper.addPlayer(roomId, userID);
        }else if (Objects.equals(isEnter, Constants.ROOM_EXIT)){
            playerMapper.removePlayer(userID);
        }
        return new Resp(HttpRespCode.SUCCESS);
    }
}
