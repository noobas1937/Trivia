package com.ecnu.trivia.web.rbac.utils;

import com.ecnu.trivia.common.component.web.servlet.HttpServletContext;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Constants;
import io.jsonwebtoken.Claims;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 当前用户操作工具
 *
 * @author Jack Chen
 */
public class UserUtils {
    private static String userKey = Constants.ONLINE_USER;
    /**
     * 获取当前用户
     */
    public static User getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession httpSession = request.getSession();
        if(ObjectUtils.isNullOrEmpty(httpSession)){
            return null;
        }
        return (User) httpSession.getAttribute(Constants.ONLINE_USER);
    }

    public static User fetchUser() {
        //保证当前用户存在,以满足授权要求
        User user = UserUtils.getCurrentUser();

        if (user == null) {
            user = User.nullUser();
        }
        return user;
    }

    public static Integer fetchUserId() {
        return UserUtils.fetchUser().getId();
    }

    public static void addUser(String current,User user){
        HttpServletRequest request = HttpServletContext.getRequest();
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(current,user);
    }

}
