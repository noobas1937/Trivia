package com.ecnu.trivia.web.message.service;

import com.ecnu.trivia.web.message.communicator.WebSocketCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * WebSocket通讯封装服务层
 * @author Jack Chen
 */
@Service
public class MessageService {
    private Logger logger = LoggerFactory.getLogger(MessageService.class);

    private WebSocketCommunicator websocketDemo = new WebSocketCommunicator();

    /**
     * 测试方法：向所有终端发送消息
     * @param message 消息
     * @return
     */
    public void sendToAllTerminal(String message){
        websocketDemo.sendMessageToUser(message);
    }
}
