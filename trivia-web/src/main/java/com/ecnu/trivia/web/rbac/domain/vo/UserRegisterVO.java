package com.ecnu.trivia.web.rbac.domain.vo;

/**
 * @Description: 注册参数VO
 * @author: Lucto Zhang
 * @Date: 22:03 2017/12/07
 */
public class UserRegisterVO {
    private String nickname;
    private String headpic;
    private String account;
    private String password;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

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
