package com.ecnu.trivia.web.game.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.service.GameService;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.question.service.QuestionService;
import com.ecnu.trivia.web.question.service.QuestionTypeService;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/game", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GameController {

    @Resource
    private GameService gameService;
    @Resource
    private RoomService roomService;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionTypeService questionTypeService;

    /**
     * 用户准备（取消准备）
     * @author: Lucto Zhang
     * @date: 16:05 2017/12/11
     */
    @RequestMapping(value = "/ready/{isReady}", method = RequestMethod.GET)
    public Resp isReady(@PathVariable("isReady")Integer ready, HttpSession session) {
        if(ready != Constants.PLAYER_READY && ready != Constants.PLAYER_WAITING) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        User user = (User) session.getAttribute(Constants.ONLINE_USER);
        if (ObjectUtils.isNullOrEmpty(user)) {
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        return gameService.checkReady(user.getId(),ready);
    }

    /**
     * 用户主动请求刷新UI
     * @author Jack Chen
     */
    @RequestMapping(value = "/refresh/", method = RequestMethod.GET)
    public Resp refreshUI() {
        User user = UserUtils.fetchUser();
        if(ObjectUtils.isNullOrEmpty(user)){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        gameService.refreshUserRoom(user.getId());
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
     * 用户掷骰子
     * @author: Handsome Zhao
     * @date: 20:05 2017/12/11
     */
    @RequestMapping(value = "/roll/dice/", method = RequestMethod.GET)
    public Resp rollDice() {
        User user = UserUtils.fetchUser();
        if(ObjectUtils.isNullOrEmpty(user)){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        boolean result = gameService.rollDice(user.getId());
        if(!result) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }else{
            return new Resp(HttpRespCode.SUCCESS);
        }
    }
    /**
     * 用户快速加入游戏
     * @author: Handsome Zhao
     * @date: 22:43 2017/12/27
     */
    @RequestMapping(value = "/qucikJoin/", method = RequestMethod.POST)
    public Resp qucikJoinGameRoom() {
        User user = UserUtils.fetchUser();
        if(ObjectUtils.isNullOrEmpty(user)){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        Integer roomId = gameService.getAppropriateReadyRoomId();
        if(ObjectUtils.isNullOrEmpty(roomId)){
            return new Resp(HttpRespCode.NOT_AVAILABLE_ROOM_ERROR);
        }
        return roomService.enterRoom(roomId,user.getId());
    }

    /**
     * 获取问题类型列表
     * @author Jack Chen
     */
    @RequestMapping(value = "/question/type/", method = RequestMethod.GET)
    public Resp getQuestionTypeList(){
        return new Resp(HttpRespCode.SUCCESS,questionTypeService.getQuestionTypeList());
    }

    /**
     * 根据用户选择的类型随机选取题目
     * @author Jack Chen
     */
    @RequestMapping(value = "/question/choose/", method = RequestMethod.GET)
    public Resp getQuestionByQuestionType(@RequestParam("type")Integer questionType){
        if(ObjectUtils.isNullOrEmpty(questionType)
                ||ObjectUtils.isNullOrEmpty(UserUtils.fetchUserId())){
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        return questionService.generateRandomQuestion(UserUtils.fetchUserId(),questionType);
    }

    /**
     * 获取问题题干及选项
     */
    @RequestMapping(value = "/question/", method = RequestMethod.GET)
    public Resp getQuestionById(@RequestParam("id")Integer questionId){
        if(ObjectUtils.isNullOrEmpty(questionId)
                ||ObjectUtils.isNullOrEmpty(UserUtils.fetchUserId())){
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        return questionService.getQuestionById(UserUtils.fetchUserId(),questionId);
    }

    /**
     * 校验用户回答
     * @param answer
     * @author Jack Chen
     */
    @RequestMapping(value = "/question/answer/", method = RequestMethod.POST)
    public Resp checkQuestionAnswer(@RequestBody Integer answer) {
        if (ObjectUtils.isNullOrEmpty(answer)) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        User user = UserUtils.fetchUser();
        if(user.equals(User.nullUser())){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        return questionService.checkQuestionAnswer(user.getId(),answer);
    }
}
