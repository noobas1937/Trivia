package com.ecnu.trivia.web.question.mapper;

import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class QuestionMapperTest {
    @Resource
    private QuestionMapper questionMapper;

    @Test
    public void deleteQuestion() throws Exception {
        int DELETE_QUESTION_ID = 3;
        questionMapper.deleteQuestion(DELETE_QUESTION_ID);
        Question question = questionMapper.getQuestionById(DELETE_QUESTION_ID);
        if(ObjectUtils.isNotNullOrEmpty(question)){
            AssertJUnit.fail("删除问题："+DELETE_QUESTION_ID+" 失败！");
        }
    }

    @Test
    public void getQuestionById() throws Exception {
        int QUERY_QUESTION_ID = 3;
        Question question = questionMapper.getQuestionById(QUERY_QUESTION_ID);
        if(ObjectUtils.isNullOrEmpty(question)){
            AssertJUnit.fail("获取问题："+QUERY_QUESTION_ID+" 失败！");
        }
    }

    @Test
    public void getQuestionByUserId() throws Exception {
        int QUERY_USER_ID = 2;
        int QUERY_USER_ID_ERROR = 3;
        Question question = questionMapper.getQuestionByUserId(QUERY_USER_ID);
        if(ObjectUtils.isNullOrEmpty(question)){
            AssertJUnit.fail("获取用户："+QUERY_USER_ID+" 的问题失败！");
        }
        Question questionEmpty = questionMapper.getQuestionByUserId(QUERY_USER_ID_ERROR);
        if(ObjectUtils.isNotNullOrEmpty(questionEmpty)){
            AssertJUnit.fail("获取到用户："+QUERY_USER_ID_ERROR+" 的问题！");
        }
    }

    @Test
    public void modifyQuestion() throws Exception {
        questionMapper.modifyQuestion(3,"test","A","B",
                "C","D",1,1);
        Question question = questionMapper.getQuestionById(3);
        AssertJUnit.assertNotNull(question);
        AssertJUnit.assertEquals("test",question.getDescription());
    }

    @Test
    public void addQuestion() throws Exception {
        Integer countInit = questionMapper.getQuestionsCount();
        questionMapper.addQuestion("test","A","B",
                "C","D",1,1);
        Integer countEnd = questionMapper.getQuestionsCount();
        AssertJUnit.assertEquals(1,countEnd-countInit);
    }

    @Test
    public void getQuestionsByQuestionTypeId() throws Exception {
        int QUERY_QUESTION_TYPE_ID = 1;
        List<Question> questions = questionMapper.getQuestionsByQuestionTypeId(QUERY_QUESTION_TYPE_ID);
        if(ObjectUtils.isNullOrEmpty(questions)){
            AssertJUnit.fail("根据问题类型获取问题出错！");
        }
    }

    @Test
    public void getQuestions() throws Exception {
        List<QuestionVO> questions = questionMapper.getQuestions(1,10);
        if(ObjectUtils.isNullOrEmpty(questions)){
            AssertJUnit.fail("分页获取问题列表失败");
        }
    }

    @Test
    public void getQuestionsCount() throws Exception {
        Integer questionCount = questionMapper.getQuestionsCount();
        AssertJUnit.assertNotNull(questionCount);
        if(questionCount<0){
            AssertJUnit.fail("问题数量为负数！");
        }
    }

    @Test
    public void generateRandomQuestion() throws Exception {
        int QUERY_QUESTION_TYPE_ID_ERROR = 0;
        Integer questionIdEmpty = questionMapper.generateRandomQuestion(QUERY_QUESTION_TYPE_ID_ERROR);
        AssertJUnit.assertNull(questionIdEmpty);
        int QUERY_QUESTION_TYPE_ID = 1;
        Integer questionId = questionMapper.generateRandomQuestion(QUERY_QUESTION_TYPE_ID);
        AssertJUnit.assertNotNull(questionId);
        Question question = questionMapper.getQuestionById(questionId);
        AssertJUnit.assertNotNull(question);
    }

}