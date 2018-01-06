/**
 * Controller编写规则：
 *   1、所有路径均按节划分，每一节均为 单个 名词
 *   2、路径结尾以反斜杠结束
 *   3、符合Restful接口规范
 *        GET：请求资源，不修改服务器数据
 *        POST：向服务器新增资源或修改资源
 *        DELETE：删除服务器资源
 *   4、返回结果均以Resp对象返回，框架统一转换为json
 *   5、代码中不得出现汉字，返回信息统一规范到HttpRespCode中
 *      若Resp中没有所需要的文字，请在ConstantsMsg中添加文字
 *   6、所有函数请在头部标明作者，以便代码回溯
 *   7、使用 @Resource 注解自动装配 Service
 *
 *   @author Jack Chen
 */
package com.ecnu.trivia.web.question.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.question.service.QuestionService;
import com.ecnu.trivia.web.question.service.QuestionTypeService;
import com.ecnu.trivia.web.utils.QuestionResp;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/question", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionController {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionTypeService questionTypeService;

    /**
     * 增加问题到题库
     * @author: Lucto
     * @date: 19:51 2017/12/17
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Resp addQuestion(@RequestBody Question questionParam) {
        if (ObjectUtils.isNullOrEmpty(questionParam.getDescription()) || ObjectUtils.isNullOrEmpty(questionParam.getChooseA())
                ||  ObjectUtils.isNullOrEmpty(questionParam.getChooseB()) || ObjectUtils.isNullOrEmpty(questionParam.getChooseC())
                || ObjectUtils.isNullOrEmpty(questionParam.getChooseD()) ||ObjectUtils.isNullOrEmpty(questionParam.getAnswer())
                || ObjectUtils.isNullOrEmpty(questionParam.getTypeId())) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        questionService.addQuestion(questionParam.getDescription(),questionParam.getChooseA(),questionParam.getChooseB(),
                questionParam.getChooseC(),questionParam.getChooseD(),questionParam.getAnswer(),questionParam.getTypeId());
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
     * 删除题库中的问题
     * @author: Lucto
     * @date: 21:17 2017/12/17
     */
    @RequestMapping(value = "/{id}/", method = RequestMethod.DELETE)
    public Resp deleteQuestion(@PathVariable("id")Integer questionId) {
        if (questionId==null) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        List<Game> game = questionService.getGameByQuestionId(questionId);
        //判断当前问题是否正在游戏中被使用
        if(ObjectUtils.isNullOrEmpty(game)) {
            questionService.deleteQuestion(questionId);
            return new Resp(HttpRespCode.SUCCESS);
        }
        return new Resp(HttpRespCode.QUESTION_ARE_USED);
    }

    /**
     * 编辑系统中问题
     * @author: Lucto
     * @date: 22:24 2017/12/17
     */
    @RequestMapping(value = "/modify/", method = RequestMethod.POST)
    public Resp modifyQuestion(@RequestBody Question questionParam) {
        if (ObjectUtils.isNullOrEmpty(questionParam.getId())) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        return questionService.modifyQuestion(questionParam);
    }

    /**
     * 问题类型的查询
     * @author: Handsome Zhao
     * @date: 19:55 2017/12/17
     */
    @RequestMapping(value = "/type/", method = RequestMethod.GET)
    public QuestionResp getQuestionTypeList(@RequestParam("pno") Integer pno,
                                    @RequestParam("PAGE_SIZE") Integer PAGE_SIZE) {
        List<QuestionType> list = questionTypeService.getQuestionTypesByPage(pno,PAGE_SIZE);
        Integer count = questionTypeService.getQuestionTypesCount();
        return new QuestionResp(HttpRespCode.SUCCESS,list,count);
    }

    /**
     * 问题类型的删除
     * @author: Handsome Zhao
     * @date: 19:55 2017/12/17
     */
    @RequestMapping(value = "/type/{id}/", method = RequestMethod.DELETE)
    public Resp getQuestionTypeList(@PathVariable("id") Integer questionTypeId) {
        boolean result = questionTypeService.deleteQuestionTypeById(questionTypeId);
        if(result) {
            return new Resp(HttpRespCode.SUCCESS);
        }
        else {
            return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
        }
    }

    /**
     * 问题类型的新增
     * @author: Handsome Zhao
     * @date: 19:55 2017/12/17
     */
    @RequestMapping(value = "/type/", method = RequestMethod.POST)
    public Resp addQuestionType(@RequestParam("description") String description) {
        boolean result = questionTypeService.addQuestionType(description);
        if(result) {
            return new Resp(HttpRespCode.SUCCESS);
        }
        else {
            return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
        }
    }

    /**
     * 问题类型的修改
     * @author: Handsome Zhao
     * @date: 19:55 2017/12/17
     */
    @RequestMapping(value = "/type/modify/", method = RequestMethod.POST)
    public Resp modifyQuestionTypeName(@RequestParam("questionId") Integer questionId,
                                       @RequestParam("description") String description) {
        boolean result = questionTypeService.modifyQuestionType(questionId,description);
        if(result) {
            return new Resp(HttpRespCode.SUCCESS);
        }
        else {
            return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
        }
    }


    /**
     * 获取所有问题列表
     * @author: Lucto
     * @date: 22:59 2017/12/17
     */
    @RequestMapping(value = "/retrive/", method = RequestMethod.GET)
    public QuestionResp getAllQuestion(@RequestParam("pno") Integer pno,
                                       @RequestParam("PAGE_SIZE") Integer PAGE_SIZE) {
        List<QuestionVO> questions = questionService.getAllQuestions(pno, PAGE_SIZE);
        Integer count = questionService.getAllQuestionsCount();
        return new QuestionResp(HttpRespCode.SUCCESS,questions,count);
    }


}
