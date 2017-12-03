package com.ecnu.trivia.web.rbac.utils;

import com.ecnu.trivia.common.component.web.servlet.HttpServletContext;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Constants;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

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

        HttpServletRequest request = HttpServletContext.getRequest();
        String token = request.getHeader("token");

        //保证当前用户存在,以满足授权要求
        Claims claims = null;

        try {
            claims = JwtUtils.parseJWT(token);
        } catch (Exception e) {
            return null;
        }
        return JwtUtils.generalSubject(claims);
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

}
