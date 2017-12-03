package com.ecnu.trivia.web.rbac.domain.vo;

/**
 * @Description: 登录参数VO
 * @author: Jack Chen
 * @Date: 16:33 2017/10/12
 */
public class UserAccountVO {
    private String account;
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
