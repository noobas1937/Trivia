package com.ecnu.trivia.web.rbac.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Resp;
import com.ecnu.trivia.web.utils.json.JSONObject;
import javafx.beans.binding.ObjectExpression;
import org.apache.http.protocol.HTTP;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.testng.AssertJUnit;

import javax.annotation.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Test
    public void get_user_by_account_successfully() throws Exception {
        User successRes = sessionService.getUserByAccount("siyuan","12345678");
        AssertJUnit.assertNotNull(successRes);
    }

    @Test
    public void get_user_by_account_with_password_error() throws Exception {
        User pwdErrorRes = sessionService.getUserByAccount("siyuan","1234");
        AssertJUnit.assertNull(pwdErrorRes);
    }

    @Test
    public void get_user_by_account_with_account_not_exist() throws Exception {
        User accountErrorRes = sessionService.getUserByAccount("sisi","555");
        AssertJUnit.assertNull(accountErrorRes);
    }

    @Test
    public void get_user_by_account_without_password_successfully() throws Exception {
        User successRes = sessionService.getUserByAccountWithoutPassword("siyuan");
        AssertJUnit.assertNotNull(successRes);
    }

    @Test
    public void get_user_by_account_without_password_with_account_not_exist() throws Exception {
        User failRes = sessionService.getUserByAccountWithoutPassword("ss");
        AssertJUnit.assertNull(failRes);
    }

    @Test
    public void set_user_last_login() throws Exception {
        Timestamp last = sessionService.getUserByAccountWithoutPassword("siyuan").getLastLogin();
        sessionService.setUserLastLogin("siyuan");
        Timestamp now = sessionService.getUserByAccountWithoutPassword("siyuan").getLastLogin();
        AssertJUnit.assertNotNull(now);
        if(last == now){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void set_user_register_info_successfully() throws Exception {
        sessionService.setUserRegisterInfo("陈中岳","New","chenzhongyue","12345678");
        User successRes = sessionService.getUserByAccountWithoutPassword("chenzhongyue");
        AssertJUnit.assertNotNull(successRes);
    }

    @Test
    public void set_user_register_info_with_account_exist() throws Exception {
        String origin = sessionService.getUserByAccountWithoutPassword("guoshuyi").getNickName();
        sessionService.setUserRegisterInfo("郭丹丹","NEW","guoshuyi","123456");
        String now = sessionService.getUserByAccountWithoutPassword("guoshuyi").getNickName();
        if(!origin.equals(now)){
            AssertJUnit.fail("failed");
        }
    }

    @Test
    public void get_user_count() throws Exception {
        Integer res = sessionService.getUserCount();
        if(res == null || res < 0){
            AssertJUnit.fail("null");
        }
    }

    @Test
    public void get_user_by_id_with_id_exist() throws Exception {
        User successRes = sessionService.getUserById(2);
        AssertJUnit.assertNotNull(successRes);
    }

    @Test
    public void get_user_by_id_with_id_not_exist() throws Exception {
        User failRes = sessionService.getUserById(10000);
        AssertJUnit.assertNull(failRes);
    }

    @Test
    public void get_user_list() throws Exception {
        List users = sessionService.getUserList(1,10);
        AssertJUnit.assertNotNull(users);
    }

    @Test
    public void add_new_user_with_account_not_exist() throws Exception {
        //新添加的用户名不重复 => 返回新添加的用户
        Resp successRes = sessionService.addNewUser("haifei","123","海飞");
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),successRes.getResCode());
    }

    @Test
    public void add_new_user_with_account_exist() throws Exception {
        //新添加的用户名已存在 => 返回旧的用户
        Resp failRes = sessionService.addNewUser("siyuan","123","原子弹");
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),failRes.getResCode());
    }

    @Test
    public void delete_user_by_id_successfully() throws Exception {
        //成功删除 => 删除成功，返回成功码
        Resp successRes = sessionService.deleteUserById(1);
        AssertJUnit.assertEquals(HttpRespCode.SUCCESS.getCode(),successRes.getResCode());
    }

    @Test
    public void delete_user_by_id_when_user_in_game() throws Exception {
        //想删除的用户正在游戏中 => 不允许删除，返回错误码
        Resp failRes =  sessionService.deleteUserById(-1);
        AssertJUnit.assertEquals(HttpRespCode.OPERATE_IS_NOT_ALLOW.getCode(),failRes.getResCode());
    }

    @Test
    public void modify_user_info_with_values() throws Exception {
        User user = new User();
        user.setId(2);
        user.setPassword("12345");
        user.setHeadPic("New");
        user.setStatus(0);
        user.setUserType(0);
        user.setNickName("远远");
        user.setBalance(1000);
        sessionService.modifyUserInfo(user);
    }

    @Test
    public void modify_user_info_with_null_values() throws Exception {
        User user = new User();
        user.setId(2);
        sessionService.modifyUserInfo(user);
    }

    @Test
    public void upload_head_pic() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        request.addHeader("Content-type", "multipart/form-data");
        File output = temporaryFolder.newFile("output.png");
        FileInputStream fis = new FileInputStream(output);
        MockMultipartFile mfile = new MockMultipartFile("C:/Users/sei_z/Desktop", "output.png", "application/vnd_ms-excel", fis);
        String uri = sessionService.uploadHeadPic(mfile);
        AssertJUnit.assertNotNull(uri);
    }

    @Test
    public void upload_head_pic_with_path_not_exist() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        request.addHeader("Content-type", "multipart/form-data");
        File output = temporaryFolder.newFile("output.png");
        FileInputStream fis = new FileInputStream(output);
        MockMultipartFile mfile = new MockMultipartFile("C:/Users/sei_z/Desktop", "output.png", "application/vnd_ms-excel", fis);
        File file = new File("C://nginx/html/image");
        if(file.exists()){
            if(file.isFile()) {
                file.delete();
            }
        }
        String uri = sessionService.uploadHeadPic(mfile);
        AssertJUnit.assertNotNull(uri);
    }

    @Test
    public void get_user_in_game() throws Exception {
        List res = sessionService.getUserInGame();
        AssertJUnit.assertNotNull(res);
    }

}