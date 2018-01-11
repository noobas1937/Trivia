package com.ecnu.trivia.web.message.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.utils.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 消息通讯WEB接口
 * @author Jack Chen
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    private Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Resource
    private MessageService messageService;

    @RequestMapping(value="/all/",method= RequestMethod.GET)
    public Resp sendToAllTerminal(@RequestParam("message") String message){
        logger.info("收到用户:{}的发送请求，向所有用户发送消息：{}", UserUtils.fetchUserId(),message);
        messageService.sendToAllTerminal(message);
        return new Resp(HttpRespCode.SUCCESS);
    }
}
