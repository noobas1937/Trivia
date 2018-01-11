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
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.game.service.GameService;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.room.domain.Room;
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


@Service
public class RoomService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Resource
    private RoomMapper roomMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private GameMapper gameMapper;

    public List<RoomVO> getRoomList(){
        return roomMapper.getRoomList();
    }

    public RoomVO getRoomById(Integer id){
        return roomMapper.getRoomById(id);
    }

    public Resp deleteRoomById(Integer id){
        Integer playerCount = playerMapper.getPlayerCount(id);
        if(playerCount == 0){
            roomMapper.deleteRoomById(id);
            return new Resp(HttpRespCode.SUCCESS);
        }
        return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
    }

    public Resp addNewRoom(String name){
        Room tempRoom = roomMapper.getRoomByName(name);
        if(ObjectUtils.isNullOrEmpty(tempRoom)){
            roomMapper.addRoomByName(name);
            return new Resp(HttpRespCode.SUCCESS);
        }
        return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
    }

    public List<Room> getRoomListByPage(Integer pno,Integer PAGE_SIZE){
        Integer npno = (pno - 1) * PAGE_SIZE;
        return roomMapper.getRoomByPage(npno,PAGE_SIZE);
    }

    public Resp modifyRoomName(Integer roomId,String name){
        Room tempRoom = roomMapper.getRoomByName(name);
        if(ObjectUtils.isNullOrEmpty(tempRoom)) {
            roomMapper.modifyRoomName(roomId,name);
            return new Resp(HttpRespCode.SUCCESS);
        }
        return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
    }

    /**
     * 退出房间
     * @param userID 退出用户ID
     */
    public Resp exitRoom(Integer userID) {
        Room room = roomMapper.getRoomByUserID(userID);
        if(room.getStatus()==Constants.ROOM_PLAYING){
            return new Resp(HttpRespCode.ROOM_PLAYING);
        }
        playerMapper.removePlayer(userID);
        messageService.refreshUI(room.getId());
        messageService.refreshHall();
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
     * 进入房间
     * @param roomId 进入的房间ID
     * @param userID 退出的用户ID
     * @author Michael Chen
     */
    public Resp enterRoom(Integer roomId, Integer userID) {
        //判断房间对应的游戏是否存在
        Game game = gameMapper.getGameByRoomId(roomId);
        //若游戏不存在，则创建该游戏
        if(ObjectUtils.isNullOrEmpty(game)){
            gameMapper.addGame(roomId,-1);
        }
        Player player = playerMapper.getPlayerByUserId(userID);
        if(ObjectUtils.isNotNullOrEmpty(player)){
            //玩家已经存在，可以直接返回成功
            return new Resp(HttpRespCode.SUCCESS);
        }

        Integer count = playerMapper.getPlayerCount(roomId);
        RoomVO room = roomMapper.getRoomById(roomId);
        //房间正在游戏中
        if(room.getStatus()==Constants.ROOM_PLAYING){
            return new Resp(HttpRespCode.ROOM_PLAYING);
        }
        //玩家超限
        if (ObjectUtils.isNotNullOrEmpty(count) && count >= Constants.MAX_PLAYER_COUNT) {
            return new Resp(HttpRespCode.ROOM_FULL);
        }
        //玩家不存在，添加玩家
        playerMapper.addPlayer(roomId, userID);
        messageService.refreshUI(roomId);
        return new Resp(HttpRespCode.SUCCESS);
    }
}
