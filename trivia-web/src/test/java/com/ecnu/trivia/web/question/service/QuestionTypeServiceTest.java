package com.ecnu.trivia.web.question.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.mapper.QuestionTypeMapper;
import com.ecnu.trivia.web.utils.Resp;
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
    public void get_question_types_by_page() throws Exception {
        List<QuestionType> questionTypes = questionTypeService.getQuestionTypesByPage(1,10);
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void get_question_types() throws Exception {
        List<QuestionType> questionTypes = questionTypeService.getQuestionTypes();
        AssertJUnit.assertNotNull(questionTypes);
    }

    @Test
    public void get_question_types_count() throws Exception {
        Integer count = questionTypeService.getQuestionTypesCount();
        AssertJUnit.assertNotNull(count);
        if(count<0){
            AssertJUnit.fail();
        }
    }

    @Test
    public void delete_questionT_type_by_id() throws Exception {
        //删除不存在的问题类型 -> 返回成功
        Resp empty = questionTypeService.deleteQuestionTypeById(-100);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),empty.getResCode());

        //删除存在问题的问题类型 -> 返回失败
        Resp fail = questionTypeService.deleteQuestionTypeById(1);
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),fail.getResCode());

        //删除不存在问题的问题类型 -> 直接删除问题类型 && 返回成功
        Resp success = questionTypeService.deleteQuestionTypeById(6);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),success.getResCode());
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(6);
        AssertJUnit.assertNull(questionType);
    }

    @Test
    public void add_question_type() throws Exception {
        //添加重复的问题类型 -> 返回失败
        Resp fail = questionTypeService.addQuestionType("校园生活");
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),fail.getResCode());

        //添加一个新的问题类型 -> 添加 && 返回成功
        Resp success = questionTypeService.addQuestionType("new test");
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),success.getResCode());

        List<QuestionType> questionTypes = questionTypeMapper.getQuestionTypeByDesc("new test");
        AssertJUnit.assertNotNull(questionTypes);
        if(questionTypes.size()<1){
            //问题未添加
            AssertJUnit.fail();
        }
    }

    @Test
    public void modify_question_type() throws Exception {
        //修改不存在的问题类型 -> 直接返回成功
        Resp failEmpty = questionTypeService.modifyQuestionType(-100,"new test");
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),failEmpty.getResCode());

        //修改为一个已经存在的问题类型 -> 返回失败
        Resp fail = questionTypeService.modifyQuestionType(1,"爱情");
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),fail.getResCode());

        //正常修改一个问题类型 -> 修改并返回成功
        questionTypeService.modifyQuestionType(1,"new test");
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(1);
        AssertJUnit.assertNotNull(questionType);
        AssertJUnit.assertEquals("new test",questionType.getName());
    }

}