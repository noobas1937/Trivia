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
package com.ecnu.trivia.web.question.service;

import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.web.game.mapper.RoomMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class QuestionService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Resource
    private RoomMapper roomMapper;

    public User getUserByAccount(String account,String password){
        User user = roomMapper.getUserByAccount(account,password);
        user.setPassword("");
        return user;
    }

    public User getUserById(Integer id){
        User user = roomMapper.getUserById(id);
        user.setPassword("");
        return user;
    }
}
