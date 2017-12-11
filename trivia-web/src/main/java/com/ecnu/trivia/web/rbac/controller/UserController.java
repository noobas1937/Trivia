package com.ecnu.trivia.web.rbac.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Resource
    private SessionService sessionService;


    /**
     * 获取当前用户资料
     * @return
     */
    @RequestMapping(value = "/currentUser/", method = RequestMethod.GET)
    public Resp getCurrentUser() {
        User user = UserUtils.fetchUser();
        if(user.equals(User.nullUser())){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        } else {
            User u = sessionService.getUserById(user.getId());
            return new Resp(HttpRespCode.SUCCESS,u);
        }
    }
}
