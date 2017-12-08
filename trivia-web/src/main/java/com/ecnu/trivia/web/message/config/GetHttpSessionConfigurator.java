package com.ecnu.trivia.web.message.config;

import com.ecnu.trivia.common.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

/**
 * 重写配置文件，以获取HTTPSession
 * @author Jack Chen
 */
public class GetHttpSessionConfigurator extends Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec,
                                HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession=(HttpSession) request.getHttpSession();
        if(ObjectUtils.isNullOrEmpty(httpSession)){ return; }
        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}