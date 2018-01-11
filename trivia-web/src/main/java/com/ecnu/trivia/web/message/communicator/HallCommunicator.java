package com.ecnu.trivia.web.message.communicator;

import com.alibaba.fastjson.JSON;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.message.config.GetHttpSessionConfigurator;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 通讯器
 * @author Jack Chen
 */
@ServerEndpoint(value="/ws/hall/",configurator = GetHttpSessionConfigurator.class)
public class HallCommunicator {
    private static Logger logger = LoggerFactory.getLogger(HallCommunicator.class);

    /**全局唯一，用户列表*/
    public static Map<Integer, HallCommunicator> ONLINE_USER = new HashMap<>();

    /**每个类私有的属性，每个在线用户都拥有一个*/
    private HttpSession httpSession;
    private Session session;
    private User user;

    /**
     * WebSocket 连接建立时的操作(重复登录时断开旧的连接)
     * @param session WebSocket连接的session属性
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) throws IOException{
        this.httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.session = session;
        if(ObjectUtils.isNullOrEmpty(httpSession)
                ||ObjectUtils.isNullOrEmpty(httpSession.getAttribute(Constants.ONLINE_USER))){
            logger.info("获取Session失败");
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"获取Session失败"));
            return;
        }
        this.user = (User) httpSession.getAttribute(Constants.ONLINE_USER);
        Integer id= user.getId();
        if (ONLINE_USER.containsKey(this.user.getId())) {
            //断开旧连接
            logger.info("当前用户id:{}已有其他终端登录,强退旧session",id);
            ONLINE_USER.get(this.user.getId()).session.close(
                    new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"已有其他终端登录"));
            ONLINE_USER.remove(this.user.getId());
        }
        logger.info("用户id:{}进入大厅",id);
        ONLINE_USER.put(id, this);
        session.getBasicRemote().sendText(JSON.toJSONString(this.user));
        logger.info("当前大厅用户数为：{}",ONLINE_USER.size());
    }

    /**
     * 连接关闭的操作
     */
    @OnClose
    public void onClose() throws IOException {
        if(ObjectUtils.isNotNullOrEmpty(user)){
            Integer id= user.getId();
            if (ONLINE_USER.containsKey(id)) {
                logger.info("当前用户id:{}退出大厅",id);
                ONLINE_USER.remove(id);
            } else {
                logger.info("当前用户id:{}重复退出，不予响应",id);
            }
        }
        logger.info("当前大厅用户数为：{}",ONLINE_USER.size());
    }

    /**
     * 收到消息后的操作
     * @param message 收到的消息
     * @param session 该连接的session属性
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if(ObjectUtils.isNotNullOrEmpty(user)){
            logger.info("收到来自用户id为：{}的消息：{}",user.getId(),message);
        }
        if(session == null) {
            logger.info("session null");
        }
    }

    /**
     * 连接发生错误时候的操作
     * @param session 该连接的session
     * @param error 发生的错误
     */
    @OnError
    public void onError(Session session, Throwable error){
        if(ObjectUtils.isNotNullOrEmpty(user)){
            logger.info("用户id为：{}的连接发送错误",user.getId());
        }
        error.printStackTrace();
    }

    /**
     * 向用户发送消息
     * @param message 发送的消息
     */
    public void sendMessageToUser(String message,Integer userId){
        HallCommunicator communicator = ONLINE_USER.get(userId);
        if (ObjectUtils.isNotNullOrEmpty(communicator)
                &&ObjectUtils.isNotNullOrEmpty(communicator.session)) {
            try {
                communicator.session.getBasicRemote().sendText(message);
                logger.info(" 给用户id为：{}的终端发送消息：{}",communicator.user.getId(),message);
            } catch (IOException e) {
                logger.error(" 给用户id为：{}的终端发送消息：{} 发送失败",communicator.user.getId(),message);
                e.printStackTrace();
            }
        }
    }

}
