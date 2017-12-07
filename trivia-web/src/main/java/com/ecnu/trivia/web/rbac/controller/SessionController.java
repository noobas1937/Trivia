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
package com.ecnu.trivia.web.rbac.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.domain.vo.UserAccountVO;
import com.ecnu.trivia.web.rbac.domain.vo.UserRegisterVO;
import com.ecnu.trivia.web.rbac.service.SessionService;
import com.ecnu.trivia.web.rbac.utils.JwtUtils;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/session", produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionController {
    @Resource
    protected SessionService sessionService;

    /**
     * @Description: 登录
     * @Author: Lucto Zhang
     * @Date: 20:30 2017/12/07
     */
    @RequestMapping(value = "/login/", method = RequestMethod.POST)
    public Resp login(@RequestBody UserAccountVO userParam,HttpSession session) {
        if (ObjectUtils.isNullOrEmpty(userParam.getAccount()) || ObjectUtils.isNullOrEmpty(userParam.getPassword())) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        User user = sessionService.getUserByAccount(userParam.getAccount(),userParam.getPassword());
        if(ObjectUtils.isNullOrEmpty(user)){
            return new Resp(HttpRespCode.USER_PASS_NOT_MATCH);
        }
        session.setAttribute("user",user);
        sessionService.setUserLastLogin(userParam.getAccount());
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
    * @Description: 登出
    * @Author: Jack Chen
    * @Date: 16:29 2017/10/12
    */
    @RequestMapping(value = "/logout/", method = RequestMethod.GET)
    public Resp logout(HttpSession session) {
        session.invalidate();
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
     * @Description: 注册
     * @Author: Lucto Zhang
     * @Date: 22:24 2017/12/07
     */
    @RequestMapping(value = "/register/", method = RequestMethod.POST)
    public Resp register(HttpSession session, @RequestBody UserRegisterVO userParam) {
        if (ObjectUtils.isNullOrEmpty(userParam.getNickname()) || ObjectUtils.isNullOrEmpty(userParam.getHeadpic()) || ObjectUtils.isNullOrEmpty(userParam.getAccount()) || ObjectUtils.isNullOrEmpty(userParam.getPassword())) {
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        sessionService.setUserRegisterInfo(userParam.getNickname(),userParam.getHeadpic(),userParam.getAccount(),userParam.getPassword());
        User user = sessionService.getUserByAccount(userParam.getAccount(),userParam.getPassword());
        session.setAttribute("user",user);
        return new Resp(HttpRespCode.SUCCESS);
    }
}
