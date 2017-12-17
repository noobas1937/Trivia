package com.ecnu.trivia.web.game.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.service.GameService;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/game", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GameController {

    @Resource
    protected GameService gameService;

    /**
     * 用户准备（取消准备）
     * @author: Lucto Zhang
     * @Date: 16:05 2017/12/11
     */
    @RequestMapping(value = "/ready/{isReady}", method = RequestMethod.GET)
    public Resp isReady(@PathVariable("isReady")Integer ready, HttpSession session) {
        if(ready == Constants.PLAYER_READY || ready == Constants.PLAYER_WAITING) {
            User user = (User) session.getAttribute(Constants.ONLINE_USER);
            if (!ObjectUtils.isNullOrEmpty(user)) {
                int userId = user.getId();
                gameService.isReady(userId, ready);
            }
        }
        return new Resp(HttpRespCode.SUCCESS);
    }

   /**
     * 用户掷骰子
     * @Author: Handsome Zhao
     * @Date: 20:05 2017/12/11
     */
    @RequestMapping(value = "/roll/dice/", method = RequestMethod.GET)
    public Resp rollDice() {
        User user = UserUtils.fetchUser();
        if(!ObjectUtils.isNullOrEmpty(user)){
            boolean result = gameService.rollDice(user.getId());
            if(!result) {
                return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
            }else{
                return new Resp(HttpRespCode.SUCCESS);
            }
        }
        return new Resp(HttpRespCode.USER_NOT_LOGIN);
    }
}
