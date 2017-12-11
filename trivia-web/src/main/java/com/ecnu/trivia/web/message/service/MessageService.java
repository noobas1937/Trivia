package com.ecnu.trivia.web.message.service;

import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.message.communicator.WebSocketCommunicator;
import com.ecnu.trivia.web.rbac.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        if(user==null||user.getId()==null) { throw new IllegalArgumentException(); }
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
                communicator.sendMessageToUser(message,userId);
            }
        }
    }
}
