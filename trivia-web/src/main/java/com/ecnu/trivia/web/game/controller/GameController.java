package com.ecnu.trivia.web.game.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.service.GameService;
import com.ecnu.trivia.web.rbac.domain.User;
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
@RequestMapping(value = "/game", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class GameController {

    @Resource
    protected GameService gameService;

    /**
     * @Description: 用户准备（取消准备）
     * @Author: Lucto Zhang
     * @Date: 16:05 2017/12/11
     */
    @RequestMapping(value = "/ready/{isReady}}", method = RequestMethod.GET)
    public Resp isReady(@PathVariable("isReady")Integer ready, HttpSession session) {
        User user = (User) session.getAttribute(Constants.ONLINE_USER);
        if (!ObjectUtils.isNullOrEmpty(user)) {
            int userId = user.getId();
            gameService.isReady(userId, ready);
        }
        return new Resp(HttpRespCode.SUCCESS);
    }
}
