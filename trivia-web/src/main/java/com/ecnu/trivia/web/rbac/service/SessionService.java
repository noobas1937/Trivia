/**
 * Service编写规范：
 *  1、所有Service类需要添加 @Service 注解标识
 *  2、所有Service类需要实现 Logable 接口
 *  3、所有Service在类内部使用 @Resource 自动注入 Mapper、Service
 *  4、如果有URL或配置常量请使用 @Value("${name}")自动装配
 *  5、所有 Service 类请添加 logger 日志器，并熟练使用logger
 *
 * @author Jack Chen
 */
package com.ecnu.trivia.web.rbac.service;

import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Service("sessionService")
public class SessionService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Value("${file.path}")
    private String path;

    @Value("${file.url}")
    private String url;

    @Resource
    private UserMapper userMapper;

    public User getUserByAccount(String account,String password){
        User user = userMapper.getUserByAccount(account,password);
        return user;
    }

    public User getUserByAccountWithoutPassword(String account){
        User user = userMapper.getUserByAccountWithoutPassword(account);
        return user;
    }

    public void setUserLastLogin(String account){
        userMapper.setLastLogin(account);
    }

    public void setUserRegisterInfo(String nickname,String headpic,String account,String password){
        userMapper.setUserRegisterInfo(nickname, headpic, account, password);
    }

    public User getUserById(Integer id){
        User user = userMapper.getUserById(id);
        user.setPassword("");
        return user;
    }

    public String uploadHeadPic(MultipartFile file){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

        /** 根据真实路径创建目录* */
        File logoSaveFile = new File(path);
        if (!logoSaveFile.exists()) {
            logoSaveFile.mkdirs();
        }
        /** 获取文件的后缀* */
        String suffix = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."));
        /** 使用UUID生成文件名称* */
        // 构建文件名称
        String logImageName = UUID.randomUUID().toString() + suffix;
        /** 拼成完整的文件保存路径加文件* */
        String fileName = path + File.separator + logImageName;
        File files = new File(fileName);
        try {
            file.transferTo(files);
        } catch (IllegalStateException e) {
            return "1001";
        } catch (IOException e) {
            return "1002";
        }
        /** 打印出上传到服务器的文件的绝对路径* */
        System.out.println("****************"+fileName+"**************");
        String uri = url+ "/"+logImageName;

        return uri;
    }
}
