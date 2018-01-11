package com.ecnu.trivia.web.rbac.mapper;

import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Lucto Zhang
 * @date 23:01 2018/01/04
 */

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@Transactional
public class UserMapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void get_user_by_account() throws Exception {
        User res = userMapper.getUserByAccount("siyuan","12345678");
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void get_user_by_account_without_password() throws Exception {
        User res = userMapper.getUserByAccountWithoutPassword("siyuan");
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void get_user_by_id() throws Exception {
        User res = userMapper.getUserById(2);
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void set_last_login() throws Exception {
        Timestamp last = userMapper.getUserByAccountWithoutPassword("siyuan").getLastLogin();
        userMapper.setLastLogin("siyuan");
        Timestamp now = userMapper.getUserByAccountWithoutPassword("siyuan").getLastLogin();
        if(last == now){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void set_user_register_info() throws Exception {
        userMapper.setUserRegisterInfo("小红军","ThisIsAHeadPic","xiaohongjun","123456789");
        User res = userMapper.getUserByAccountWithoutPassword("xiaohongjun");
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void getUserList() throws Exception {
        List res = userMapper.getUserList(1,10);
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void add_new_user() throws Exception {
        userMapper.addNewUser("liqing","12345678","李钦儿","MissLi'sHeadPic");
        User res = userMapper.getUserByAccountWithoutPassword("liqing");
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void delete_user_by_id() throws Exception {
        userMapper.deleteUserById(8);
        User res = userMapper.getUserById(8);
        if(!ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }

    }

    @Test
    public void modify_user_info_with_new_password() throws Exception {
        User last = userMapper.getUserById(2);
        User tmp= new User();
        tmp.transform(last);
        tmp.setId(2);
        tmp.setNickName("远远");
        tmp.setPassword("123456");
        userMapper.modifyUserInfo(tmp);
        User now = userMapper.getUserById(2);
        AssertJUnit.assertNotNull(now);
        if(last.getNickName().equals(now.getNickName())){
                AssertJUnit.fail("failed");
        }
    }

    @Test
    public void modify_user_info_without_new_password() throws Exception {
        User last = userMapper.getUserById(3);
        User tmp = new User();
        tmp.transform(last);
        tmp.setId(3);
        tmp.setNickName("阿晨");
        userMapper.modifyUserInfo(tmp);
        User now = userMapper.getUserById(3);
        AssertJUnit.assertNotNull(now);
        if(last.getNickName().equals(now.getNickName())){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void upload() throws Exception {
        User last = userMapper.getUserByAccountWithoutPassword("siyuan");
        userMapper.upload("siyuan","NewHeadPic");
        User now = userMapper.getUserByAccountWithoutPassword("siyuan");
        if(ObjectUtils.isNullOrEmpty(now)){
            AssertJUnit.fail("null");
        }
        if(last.getHeadPic().equals(now.getHeadPic())){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void get_user_count() throws Exception {
        Integer res = userMapper.getUserCount();
        if(res == null || res < 0){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void getUserInGame() throws Exception {
        List res = userMapper.getUserInGame();
        if(ObjectUtils.isNullOrEmpty(res)){
            AssertJUnit.fail("null");
        }
    }

}