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
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class QuestionService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private GameMapper gameMapper;

    /**
     * 为系统增加问题
     * @Author: Lucto
     * * @Date: 19:51 2017/12/17
     */
    public void addQuestion(String content,String chooseA,String chooseB,String chooseC,String chooseD,Integer answer,Integer type){
        questionMapper.addQuestion(content,chooseA,chooseB,chooseC,chooseD,answer,type);
    }

    /**
     * 通过问题id获取游戏
     * @Author: Lucto
     * * @Date: 21:20 2017/12/17
     */
    public Game getGameByQuestionId(Integer questionId){
        return gameMapper.getGameByQuestionId(questionId);
    }

    /**
     * 删除问题
     * @Author: Lucto
     * * @Date: 21:39 2017/12/17
     */
    public void deleteQuestion(Integer questionId) {
        questionMapper.deleteQuestion(questionId);
    }

}
