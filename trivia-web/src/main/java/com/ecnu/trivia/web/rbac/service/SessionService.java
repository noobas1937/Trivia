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

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.domain.vo.UserGameVO;
import com.ecnu.trivia.web.rbac.mapper.UserMapper;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
public class SessionService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Value("${file.path}")
    private String path;

    @Value("${file.url}")
    private String url;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PlayerMapper playerMapper;

    public User getUserByAccount(String account,String password){
        return userMapper.getUserByAccount(account,password);
    }

    public User getUserByAccountWithoutPassword(String account){
        return userMapper.getUserByAccountWithoutPassword(account);
    }

    public void setUserLastLogin(String account){
        userMapper.setLastLogin(account);
    }

    public void setUserRegisterInfo(String nickname,String headpic,String account,String password){
        userMapper.setUserRegisterInfo(nickname, headpic, account, password);
    }

    public Integer getUserCount(){
        return userMapper.getUserCount();
    }

    public User getUserById(Integer id){
        User user = userMapper.getUserById(id);
        if(ObjectUtils.isNullOrEmpty(user)){
            return user;
        }
        user.setPassword("");
        return user;
    }

    public List<User> getUserList(Integer pno,Integer pageSize){
        Integer pageNumber = (pno - 1) * pageSize;
        return userMapper.getUserList(pageNumber,pageSize);
    }

    public Resp addNewUser(String account,String password,
                              String nickName){
        //查看是否有account相同的user账号,重复的话不能添加并返回false，不重复的话不允许添加
        User tempUser = userMapper.getUserByAccountWithoutPassword(account);
        if(ObjectUtils.isNullOrEmpty(tempUser)){
            userMapper.addNewUser(account, password, nickName, Constants.defaultHeadPic);
            return new Resp(HttpRespCode.SUCCESS);
        }
        return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
    }

    public Resp deleteUserById(Integer userId){
        Player tempPlayer = playerMapper.getPlayerByUserId(userId);
        //查看尝试删除的user相联的player是否存在，若存在的话则不删除
        if(ObjectUtils.isNullOrEmpty(tempPlayer)){
            userMapper.deleteUserById(userId);
                return new Resp(HttpRespCode.SUCCESS);
        }
        return new Resp(HttpRespCode.OPERATE_IS_NOT_ALLOW);
    }

    /**
     * //TODO:重新单元测试
     * @param userParam
     * @author Michael Chen
     */
    public void modifyUserInfo(User userParam) {
        User user = userMapper.getUserById(userParam.getId());
        userParam.transform(user);
        userMapper.modifyUserInfo(userParam);
    }

    public String uploadHeadPic(MultipartFile file){
        /* 根据真实路径创建目录* */
        File logoSaveFile = new File(path);
        if (!logoSaveFile.exists()) {
            logoSaveFile.mkdirs();
        }
        /* 获取文件的后缀* */
        String suffix = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."));
        /* 使用UUID生成文件名称* */
        // 构建文件名称
        String logImageName = UUID.randomUUID().toString() + suffix;
        /* 拼成完整的文件保存路径加文件* */
        String fileName = path + "/"+ logImageName;
        File files = new File(fileName);
        try {
            file.transferTo(files);
        } catch (IllegalStateException e) {
            return "1001";
        } catch (IOException e) {
            return "1002";
        }
        /* 打印出上传到服务器的文件的绝对路径* */
        return url+ "/"+logImageName;
    }

    public List<UserGameVO> getUserInGame(){
        return userMapper.getUserInGame();
    }
}
