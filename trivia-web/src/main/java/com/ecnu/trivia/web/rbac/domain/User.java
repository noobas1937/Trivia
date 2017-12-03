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
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

@Table("user")
public class User extends Domain<User>{
    /**
     * 表示一个不存在的用户
     */
    public static final int USER_ID_NULL = -1000;

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

    @Column(value = "head_img", jdbcType = JdbcType.VARCHAR)
    private String headImg;

    @Column(value = "user_type", jdbcType = JdbcType.INTEGER)
    private int userType;

    @Column(value = "user_name", jdbcType = JdbcType.VARCHAR)
    private String userName;

    @Column(value = "mobile", jdbcType = JdbcType.VARCHAR)
    private String mobile;

    @Column(value = "company_id", jdbcType = JdbcType.INTEGER)
    private int companyId;

    @Column(value = "status", jdbcType = JdbcType.INTEGER)
    private int status;

    @Column(value = "last_login", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp lastLogin;

    @Column(value = "gmt_create", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreate;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

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

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public User() {
        super();
    }

    public User(Integer id) {
        this.id = id;
    }

    /**
     * 构建相应的null用户
     */
    public static User nullUser() {
        return nullUser;
    }

    @Override
    public Key key()
    {
        return Key.of(id);
    }

    @Override
    public void clearKey() {
        id=0;
    }
}
