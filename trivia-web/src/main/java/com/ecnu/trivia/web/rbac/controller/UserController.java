package com.ecnu.trivia.web.rbac.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.domain.vo.UserGameVO;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.QuestionResp;
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
    public QuestionResp getUserList(@RequestParam("pno") Integer pno, @RequestParam("PAGE_SIZE") Integer PAGE_SIZE,HttpSession session) {
        User currentUser = (User)session.getAttribute(Constants.ONLINE_USER);
        if(currentUser.getUserType()==0) {
            List<User> list = sessionService.getUserList(pno,PAGE_SIZE);
            Integer count = sessionService.getUserCount();
            return new QuestionResp(HttpRespCode.SUCCESS, list,count);
        }
        return new QuestionResp(HttpRespCode.USER_NO_JURISDICTION);
    }

    /*增加用户*/
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Resp addNewUser(@RequestBody User user){
        return sessionService.addNewUser(user.getAccount(), user.getPassword(), user.getNickName());
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
        sessionService.modifyUserInfo(user.getId(),user.getPassword(),user.getNickName(),user.getHeadPic(),user.getUserType(),user.getStatus(),user.getBalance());
        return new Resp(HttpRespCode.SUCCESS);
    }


    /**
     * 获取游戏中玩家列表
     * @return
     */
    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public Resp getUserInGame() {
        List<UserGameVO> userInGame = sessionService.getUserInGame();
        if(ObjectUtils.isNullOrEmpty(userInGame)){
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        return new Resp(HttpRespCode.SUCCESS,userInGame);
    }

}
