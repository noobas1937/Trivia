package com.ecnu.trivia.web.common.websocket;

import com.ecnu.trivia.web.rbac.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WebSocket消息处理器
 * @author Jack Chen
 */
public class WebSocketHandler extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(SessionService.class);
    /**存储在线用户 */
    private final static List<WebSocketSession> SESSIONS = Collections.synchronizedList(new ArrayList<WebSocketSession>());

    //消息处理
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    //连接建立后处理
    @SuppressWarnings("unchecked")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("session:"+session.getId()+" | connect to the websocket success......");
        SESSIONS.add(session);
    }

    //抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        logger.debug("session:"+session.getId()+" | connection closed......");
        SESSIONS.remove(session);
    }

    //连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.debug("session:"+session.getId()+" | connection closed......");
        SESSIONS.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
