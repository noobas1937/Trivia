/**
 * Domain对象编写规范：
 *  1、所有成员变量按照驼峰式命名结构命名
 *  2、所有成员变量需要与数据库字段进行一一绑定
 *  3、所有成员变量需要绑定数据类型：INTEGER、VARCHAR、TIMESTAMP、NUMERIC
 *  4、为所有成员变量生成 set/get 方法
 *  5、生成构造函数：默认构造函数 + 主键构造函数
 *  6、继承Domain类，并重写 Key() + ClearKey()函数
 *  7、注意类需使用 @Table 注解标明对应映射数据库表
 */
package com.ecnu.trivia.web.rbac.domain;

import com.ecnu.trivia.common.component.domain.*;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.utils.MD5Util;
import org.apache.ibatis.type.JdbcType;
import sun.security.provider.MD5;

import java.sql.Timestamp;

/**
 * 用户实体类
 * @author Jack Chen
 */
@Table("user")
public class User{
    /**
     * 表示一个不存在的用户
     */
    public static final Integer USER_ID_NULL = -1000;

    /**
     * 构建一个表示没有当前用户的用户(即userId为null)
     */
    private static final User nullUser = new User(USER_ID_NULL);

    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "account", jdbcType = JdbcType.VARCHAR)
    private String account;

    @Column(value = "password", jdbcType = JdbcType.VARCHAR)
    private String password;

    @Column(value = "nick_name", jdbcType = JdbcType.VARCHAR)
    private String nickName;

    @Column(value = "head_pic", jdbcType = JdbcType.VARCHAR)
    private String headPic;

    @Column(value = "balance", jdbcType = JdbcType.INTEGER)
    private Integer balance;

    @Column(value = "status", jdbcType = JdbcType.INTEGER)
    private Integer status;

    @Column(value = "user_type", jdbcType = JdbcType.INTEGER)
    private Integer userType;

    @Column(value = "last_login", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp lastLogin;

    @Column(value = "gmt_created", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Timestamp getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Timestamp gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 构建相应的null用户
     */
    public static User nullUser() {
        return nullUser;
    }

    /**
     * 用户资料修改替换工具
     * @param user 用户旧资料
     */
    public void transform(User user) {
        if(ObjectUtils.isNullOrEmpty(nickName)){
            nickName = user.getNickName();
        }
        if(ObjectUtils.isNullOrEmpty(headPic)){
            headPic = user.getHeadPic();
        }
        if(ObjectUtils.isNullOrEmpty(userType)){
            userType = user.getUserType();
        }
        if(ObjectUtils.isNullOrEmpty(status)){
            status = user.getStatus();
        }
        if(ObjectUtils.isNullOrEmpty(balance)){
            balance = user.getBalance();
        }
        //因为MD5加密不可逆，所以有两种修改情况
        if(ObjectUtils.isNullOrEmpty(password)){
            password = user.getPassword();
        }else{
            password = MD5Util.md5(password);
        }
    }

}
