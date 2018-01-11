package com.ecnu.trivia.web.message.service;

import com.alibaba.fastjson.JSON;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.vo.PlayerVO;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.message.communicator.HallCommunicator;
import com.ecnu.trivia.web.message.communicator.WebSocketCommunicator;
import com.ecnu.trivia.web.message.domain.WSDatagram;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.json.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WebSocket通讯封装服务层
 * @author Jack Chen
 */
@Service
public class MessageService {
    private Logger logger = LoggerFactory.getLogger(MessageService.class);

    private Map<Integer, WebSocketCommunicator> onlineUser = WebSocketCommunicator.ONLINE_USER;
    private Map<Integer, HallCommunicator> hallOnlineUser = HallCommunicator.ONLINE_USER;

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
     * 向所有终端发送消息
     * @param message 消息
     * @return
     */
    public void sendToAllHallTerminal(String message){
        for (Integer userId : hallOnlineUser.keySet()) {
            hallOnlineUser.get(userId).sendMessageToUser(message,userId);
        }
    }
    /**
     * 向所有终端发送消息(除了自己)
     * @param message 消息
     * @param userId 除去的user
     * @return
     */
    public void sendToAllHallTerminal(String message,Integer userId){
        for (Integer uid : hallOnlineUser.keySet()) {
            if(Objects.equals(uid, userId)){ continue; }
            hallOnlineUser.get(uid).sendMessageToUser(message,uid);
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
            if(ObjectUtils.isNullOrEmpty(userId)){ continue; }
            WebSocketCommunicator communicator = onlineUser.get(player.getUserId());
            if(ObjectUtils.isNullOrEmpty(communicator)){ continue; }
            communicator.sendMessageToUser(message,userId);
        }
    }

    /**
     * 刷新游戏界面
     * PreCondition: 房间处于游戏状态
     * PostCondition: 根据数据库发送刷新UI数据包
     * @param roomId 房间ID
     */
    public boolean refreshUI(Integer roomId){
        if(ObjectUtils.isNullOrEmpty(roomId)){
            return false;
        }
        RoomVO room = roomService.getRoomById(roomId);
        Game game = gameMapper.getGameByRoomId(roomId);
        room.setGame(game);
        sendMsgToPlayers(JSON.toJSONString(room),room.getPlayerList());
        return true;
    }

    /**
     * 刷新大厅界面
     * @return
     */
    public boolean refreshHall(){
        WSDatagram datagram = new WSDatagram(Constants.DATAGRAM_PLAYERS_CHANGE);
        sendToAllHallTerminal(JSONUtils.toJsonString(datagram));
        return true;
    }
}
