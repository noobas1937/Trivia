package com.ecnu.trivia.web.rbac.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Resp;
import javafx.beans.binding.ObjectExpression;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author: Lucto Zhang
 * @date: 23:01 2018/01/04
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class SessionServiceTest {
    @Resource
    private SessionService sessionService;

    @Test
    public void getUserByAccount() throws Exception {
        User successRes = sessionService.getUserByAccount("siyuan","12345678");
        AssertJUnit.assertNotNull(successRes);
        User pwdErrorRes = sessionService.getUserByAccount("siyuan","1234");
        AssertJUnit.assertNull(pwdErrorRes);
        User accountErrorRes = sessionService.getUserByAccount("sisi","555");
        AssertJUnit.assertNull(accountErrorRes);
    }

    @Test
    public void getUserByAccountWithoutPassword() throws Exception {
        User successRes = sessionService.getUserByAccountWithoutPassword("siyuan");
        AssertJUnit.assertNotNull(successRes);
        User failRes = sessionService.getUserByAccountWithoutPassword("ss");
        AssertJUnit.assertNull(failRes);
    }

    @Test
    public void setUserLastLogin() throws Exception {
        Timestamp last = sessionService.getUserByAccountWithoutPassword("siyuan").getLastLogin();
        sessionService.setUserLastLogin("siyuan");
        Timestamp now = sessionService.getUserByAccountWithoutPassword("siyuan").getLastLogin();
        AssertJUnit.assertNotNull(now);
        if(last == now){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void setUserRegisterInfo() throws Exception {
        sessionService.setUserRegisterInfo("陈中岳","New","chenzhongyue","12345678");
        User successRes = sessionService.getUserByAccountWithoutPassword("chenzhongyue");
        AssertJUnit.assertNotNull(successRes);
        String origin = sessionService.getUserByAccountWithoutPassword("guoshuyi").getNickName();
        sessionService.setUserRegisterInfo("郭丹丹","NEW","guoshuyi","123456");
        String now = sessionService.getUserByAccountWithoutPassword("guoshuyi").getNickName();
        if(!origin.equals(now)){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void getUserCount() throws Exception {
        Integer res = sessionService.getUserCount();
        if(res == null || res < 0){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void getUserById() throws Exception {
        User successRes = sessionService.getUserById(2);
        AssertJUnit.assertNotNull(successRes);
        User failRes = sessionService.getUserById(10000);
        AssertJUnit.assertNull(failRes);
    }

    @Test
    public void getUserList() throws Exception {
        List users = sessionService.getUserList(1,10);
        AssertJUnit.assertNotNull(users);
    }

    @Test
    public void addNewUser() throws Exception {
        //新添加的用户名不重复 => 返回新添加的用户
        Resp successRes = sessionService.addNewUser("haifei","123","海飞");
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),successRes.getResCode());
        //新添加的用户名已存在 => 返回旧的用户
        Resp failRes = sessionService.addNewUser("siyuan","123","原子弹");
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),failRes.getResCode());
    }

    @Test
    public void deleteUserById() throws Exception {
        //成功删除 => 删除成功，返回成功码
        Resp successRes = sessionService.deleteUserById(1);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),successRes.getResCode());
        //想删除的用户正在游戏中 => 不允许删除，返回错误码
        Resp failRes =  sessionService.deleteUserById(-1);
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),failRes.getResCode());
    }

    @Test
    public void modifyUserInfo() throws Exception {
        sessionService.modifyUserInfo(2,"12345","远远","New",0,0,1000);
    }

    @Test
    public void uploadHeadPic() throws Exception {
    }

    @Test
    public void getUserInGame() throws Exception {
        List res = sessionService.getUserInGame();
        AssertJUnit.assertNotNull(res);
    }

}