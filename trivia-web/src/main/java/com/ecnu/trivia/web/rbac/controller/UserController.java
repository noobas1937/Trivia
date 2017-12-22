package com.ecnu.trivia.web.rbac.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import com.sun.tools.internal.jxc.ap.Const;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

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

    /**
     * 用户的CRUD
     */

    /*获得所有用户列表*/
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Resp getUserList(HttpSession session) {
        User currentUser = (User)session.getAttribute(Constants.ONLINE_USER);
        if(currentUser.getUserType()==0) {
            List<User> list = sessionService.getUserList();
            return new Resp(HttpRespCode.SUCCESS, list);
        }
        return new Resp(HttpRespCode.USER_NO_JURISDICTION);
    }

    /*增加用户*/
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Resp addNewUser(@RequestParam("account") String account,
                            @RequestParam("password") String password,
                            @RequestParam("nickName") String nickName,
                            @RequestParam("headPic") String headPic){
        return sessionService.addNewUser(account, password, nickName, headPic);
    }

    /*根据用户id删除用户*/
    @RequestMapping(value = "/{userID}/", method = RequestMethod.DELETE)
    public Resp deleteUserById(@PathVariable("userID")Integer userID) {
        return sessionService.deleteUserById(userID);
    }

    /*修改用户资料*/
    @RequestMapping(value = "/modify/", method = RequestMethod.POST)
    public Resp modifyUserInfo(@RequestBody User user){
        if(ObjectUtils.isNullOrEmpty(user.getId())){
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        sessionService.modifyUserInfo(user.getId(),user.getPassword(),user.getNickName(),user.getHeadPic());
        return new Resp(HttpRespCode.SUCCESS);
    }


}
