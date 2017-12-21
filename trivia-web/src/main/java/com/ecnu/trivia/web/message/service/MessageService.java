package com.ecnu.trivia.web.message.service;

import com.alibaba.fastjson.JSON;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.domain.vo.PlayerVO;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.service.GameService;
import com.ecnu.trivia.web.message.communicator.WebSocketCommunicator;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.ConstantsMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * WebSocket通讯封装服务层
 * @author Jack Chen
 */
@Service
public class MessageService {
    private Logger logger = LoggerFactory.getLogger(MessageService.class);

    private Map<Integer, WebSocketCommunicator> onlineUser = WebSocketCommunicator.ONLINE_USER;

    @Resource
    private RoomService roomService;
    @Resource
    private GameMapper gameMapper;

    /**
     * 向所有终端发送消息
     * @param message 消息
     * @return
     */
    public void sendToAllTerminal(String message){
        for (Integer userId : onlineUser.keySet()) {
            onlineUser.get(userId).sendMessageToUser(message,userId);
        }
    }

    /**
     * 向单一用户发送消息
     * @param message 消息
     * @param user 目标用户
     * @return
     */
    public void sendToAllTerminal(String message,User user){
        if(user==null) { throw new IllegalArgumentException(); }
        WebSocketCommunicator communicator = onlineUser.get(user.getId());

        if (ObjectUtils.isNotNullOrEmpty(communicator)) {
            communicator.sendMessageToUser(message,user.getId());
        }
    }

    /**
     * 向一组用户发送消息
     * @param message 消息内容（JSON串）
     * @param userList 目标用户列表（必须包含用户ID）
     */
    public void sendMsgToUsers(String message, List<User> userList){
        if(userList==null) { throw new IllegalArgumentException(); }
        for (User user : userList) {
            Integer userId = user.getId();
            if(ObjectUtils.isNotNullOrEmpty(userId)){
                WebSocketCommunicator communicator = onlineUser.get(user.getId());
                if(ObjectUtils.isNotNullOrEmpty(communicator)) {
                    communicator.sendMessageToUser(message, userId);
                }
            }
        }
    }

    /**
     * 向一组玩家发送消息
     * @param message 消息内容（JSON串）
     * @param players 目标用户列表（必须包含用户ID）
     */
    private void sendMsgToPlayers(String message, List<PlayerVO> players){
        if(players==null) { throw new IllegalArgumentException(); }
        for (PlayerVO player : players) {
            Integer userId = player.getUserId();
            if(ObjectUtils.isNotNullOrEmpty(userId)){
                WebSocketCommunicator communicator = onlineUser.get(player.getUserId());
                if(ObjectUtils.isNotNullOrEmpty(communicator)){
                    communicator.sendMessageToUser(message,userId);
                }
            }
        }
    }

    /**
     * 刷新房间界面
     * PreCondition: 房间处于游戏状态
     * PostCondition: 根据数据库发送刷新UI数据包
     * @param roomId 房间ID
     */
    public boolean refreshUI(Integer roomId){
        if(ObjectUtils.isNullOrEmpty(roomId)){
            return false;
        }
        RoomVO room = roomService.getRoomById(roomId);
        Game game = gameMapper.getGameById(roomId);
        room.setGame(game);
        sendMsgToPlayers(JSON.toJSONString(room),room.getPlayerList());
        return true;
    }
}
