package com.ecnu.trivia.web.question.mapper;

import com.ecnu.trivia.web.question.domain.QuestionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jack Chen
 * @date 2018/1/4
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class QuestionTypeMapperTest {

    @Resource
    private QuestionTypeMapper questionTypeMapper;

    @Test
    public void get_question_types() throws Exception {
        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypes();
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void get_question_types_by_page() throws Exception {
        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypesByPage(1,10);
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void getQuestionTypeByDesc() throws Exception {
        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypeByDesc("爱情");
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void delete_question_type_by_id() throws Exception {
        questionTypeMapper.deleteQuestionTypeById(6);
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(6);
        AssertJUnit.assertNull(questionType);
    }

    @Test
    public void add_question_type() throws Exception {
        questionTypeMapper.addQuestionType("哲学");
        List<QuestionType> questionType =  questionTypeMapper.getQuestionTypeByDesc("爱情");
        AssertJUnit.assertNotNull(questionType);
    }

    @Test
    public void update_question_type() throws Exception {
        questionTypeMapper.updateQuestionType(1,"爱情");
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(1);
        AssertJUnit.assertEquals("爱情",questionType.getName());
    }

    @Test
    public void get_question_type_by_id() throws Exception {
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(1);
        AssertJUnit.assertNotNull(questionType);
    }
}