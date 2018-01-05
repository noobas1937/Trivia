package com.ecnu.trivia.web.question.service;

import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.mapper.QuestionTypeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Jack Chen
 * @date 2018/1/4
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class QuestionTypeServiceTest {
    @Resource
    private QuestionTypeService questionTypeService;
    @Resource
    private QuestionTypeMapper questionTypeMapper;

    @Test
    public void getQuestionTypesByPage() throws Exception {
        List<QuestionType> questionTypes = questionTypeService.getQuestionTypesByPage(1,10);
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void getQuestionTypes() throws Exception {
        List<QuestionType> questionTypes = questionTypeService.getQuestionTypes();
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void getQuestionTypesCount() throws Exception {
        Integer count = questionTypeService.getQuestionTypesCount();
        AssertJUnit.assertNotNull(count);
        if(count<0){
            AssertJUnit.fail();
        }
    }

    @Test
    public void deleteQuestionTypeById() throws Exception {
        //删除不存在的问题类型 -> 返回成功
        boolean empty = questionTypeService.deleteQuestionTypeById(-100);
        AssertJUnit.assertEquals(true,empty);

        //删除存在问题的问题类型 -> 返回失败
        boolean fail = questionTypeService.deleteQuestionTypeById(1);
        AssertJUnit.assertEquals(false,fail);

        //删除不存在问题的问题类型 -> 直接删除问题类型 && 返回成功
        boolean success = questionTypeService.deleteQuestionTypeById(6);
        AssertJUnit.assertEquals(true,success);
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(6);
        AssertJUnit.assertNull(questionType);
    }

    @Test
    public void addQuestionType() throws Exception {
        //添加重复的问题类型 -> 返回失败
        boolean fail = questionTypeService.addQuestionType("校园生活");
        AssertJUnit.assertEquals(false,fail);

        //添加一个新的问题类型 -> 添加 && 返回成功
        boolean success = questionTypeService.addQuestionType("new test");
        AssertJUnit.assertEquals(true,success);

        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypeByDesc("new test");
        AssertJUnit.assertNotNull(questionTypes);
        if(questionTypes.size()<1){
            //问题未添加
            AssertJUnit.fail();
        }
    }

    @Test
    public void modifyQuestionType() throws Exception {
        //修改不存在的问题类型 -> 直接返回成功
        boolean failEmpty = questionTypeService.modifyQuestionType(-100,"new test");
        AssertJUnit.assertEquals(true,failEmpty);

        //修改为一个已经存在的问题类型 -> 返回失败
        boolean fail = questionTypeService.modifyQuestionType(1,"爱情");
        AssertJUnit.assertEquals(false,fail);

        //正常修改一个问题类型 -> 修改并返回成功
        questionTypeService.modifyQuestionType(1,"new test");
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(1);
        AssertJUnit.assertNotNull(questionType);
        AssertJUnit.assertEquals("new test",questionType.getName());
    }

}