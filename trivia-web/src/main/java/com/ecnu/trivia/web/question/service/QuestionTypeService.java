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

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.question.mapper.QuestionTypeMapper;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.ConstantsMsg;
import com.ecnu.trivia.web.utils.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 问题类型服务，主要提供问题类型相关的操作服务
 * @author Jack Chen
 */
@Service
public class QuestionTypeService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(QuestionTypeService.class);

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionTypeMapper questionTypeMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private MessageService messageService;

    public List<QuestionType> getQuestionTypeListByPage(@RequestParam("pno") Integer pno,
                                                  @RequestParam("PAGE_SIZE") Integer PAGE_SIZE)
    {
        Integer npno = (pno - 1) * PAGE_SIZE;
        return questionTypeMapper.getQuestionTypeListByPage(npno, PAGE_SIZE);
    }

    public List<QuestionType> getQuestionTypeList()
    {
        return questionTypeMapper.getQuestionTypeList();
    }

    public Integer getQuestionTypeListCount()
    {
        return questionTypeMapper.getQuestionTypeList().size();
    }

    public boolean deleteQuestionTypeById(Integer questionTypeId){
        //查找是否存在该类型的问题，存在的话不能删除
        List<Question> questionList = questionMapper.getQuestionListByQuestionTypeId(questionTypeId);
        if(questionList.isEmpty()){
            questionTypeMapper.deleteQuestionTypeByQuestionTypeId(questionTypeId);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addQuestionType(String description){
        //查找是否有名字重复的问题类型
        List<QuestionType> questionTypeList = questionTypeMapper.getQuestionTypeByQuestionTypeDescription(description);
        if(questionTypeList.isEmpty()){
            questionTypeMapper.addQuestionTypeByDescription(description);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean modifyQuestionTypeName(Integer questionId, String description){
        //查找是否有名字重复的问题类型
        List<QuestionType> questionTypeList = questionTypeMapper.getQuestionTypeByQuestionTypeDescription(description);
        if(questionTypeList.isEmpty()){
            questionTypeMapper.updateQuestionTypeName(questionId,description);
            return true;
        }
        else{
            return false;
        }
    }

}
