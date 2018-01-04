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

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"
        ,"classpath:spring/applicationContext-web.xml"})
@Transactional
public class QuestionTypeMapperTest {

    @Resource
    private QuestionTypeMapper questionTypeMapper;

    @Test
    public void getQuestionTypes() throws Exception {
        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypes();
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void getQuestionTypesByPage() throws Exception {
        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypesByPage(1,10);
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void getQuestionTypeByDesc() throws Exception {
        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypeByDesc("爱情");
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void deleteQuestionTypeById() throws Exception {
        questionTypeMapper.deleteQuestionTypeById(6);
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(6);
        AssertJUnit.assertNull(questionType);
    }

    @Test
    public void addQuestionType() throws Exception {
        questionTypeMapper.addQuestionType("哲学");
        List<QuestionType> questionType =  questionTypeMapper.getQuestionTypeByDesc("爱情");
        AssertJUnit.assertNotNull(questionType);
    }

    @Test
    public void updateQuestionType() throws Exception {
        questionTypeMapper.updateQuestionType(1,"爱情");
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(1);
        AssertJUnit.assertEquals("爱情",questionType.getName());
    }

    @Test
    public void getQuestionTypeById() throws Exception {
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(1);
        AssertJUnit.assertNotNull(questionType);
    }
}