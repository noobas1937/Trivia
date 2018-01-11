/**
 * Controller编写规则：
 *   1、所有路径均按节划分，每一节均为 单个 名词
 *   2、路径结尾以反斜杠结束
 *   3、符合Restful接口规范
 *        GET：请求资源，不修改服务器数据
 *        POST：向服务器新增资源或修改资源
 *        DELETE：删除服务器资源
 *   4、返回结果均以Resp对象返回，框架统一转换为json
 *   5、代码中不得出现汉字，返回信息统一规范到HttpRespCode中
 *      若Resp中没有所需要的文字，请在ConstantsMsg中添加文字
 *   6、所有函数请在头部标明作者，以便代码回溯
 *   7、使用 @Resource 注解自动装配 Service
 *
 *   @author Jack Chen
 */
package com.ecnu.trivia.web.room.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.room.service.RoomService;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/room", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RoomController {
    @Resource
    private RoomService roomService;

    /**
    * 获取房间列表
    * @author: Handsome Zhao
    * @date: 16:29 2017/12/7
    */
    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public Resp getRoomList() {
        List<RoomVO> list = roomService.getRoomList();
        return new Resp(HttpRespCode.SUCCESS,list);
    }

    /**
     * 根据id号获取特定的房间
     * @author: Handsome Zhao
     * @date: 16:29 2017/12/7
     */
    @RequestMapping(value = "/detail/", method = RequestMethod.GET)
    public Resp getRoomList(@RequestParam("roomId")Integer roomId) {
        RoomVO roomVO = roomService.getRoomById(roomId);
        return new Resp(HttpRespCode.SUCCESS,roomVO);
    }

    /**
     * 根据id号删除特定的房间
     * @author: Handsome Zhao
     * @date: 16:29 2018/1/10
     */
    @RequestMapping(value = "/{roomId}/", method = RequestMethod.DELETE)
    public Resp deleteRoom(@PathVariable("roomId") Integer roomId) {
        return roomService.deleteRoomById(roomId);
    }

    /**
     * 增加房间
     * @author: Handsome Zhao
     * @date: 16:29 2018/1/10
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Resp addNewRoom(@RequestParam("name") String name){
        return roomService.addNewRoom(name);
    }

    /**
     * 修改房间名字
     * @author: Handsome Zhao
     * @date: 16:29 2018/1/10
     */
    @RequestMapping(value = "/modify/", method = RequestMethod.POST)
    public Resp ModifyRoomName(@RequestParam("roomId") Integer roomId,@RequestParam("name") String name){
        return roomService.modifyRoomName(roomId,name);
    }

    /**
     * 进入房间接口
     * @param roomId
     * @return
     */
    @RequestMapping(value = "/enter/", method = RequestMethod.GET)
    public Resp enterRoom(@RequestParam("roomId") Integer roomId) {
        if(ObjectUtils.isNullOrEmpty(roomId)){
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        User user = UserUtils.fetchUser();
        if(user==User.nullUser()){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        return roomService.enterRoom(roomId,user.getId());
    }

    /**
     * 退出房间接口
     * @return
     */
    @RequestMapping(value = "/exit/", method = RequestMethod.POST)
    public Resp enterRoom() {
        User user = UserUtils.fetchUser();
        if(user==User.nullUser()){
            return new Resp(HttpRespCode.USER_NOT_LOGIN);
        }
        return roomService.exitRoom(user.getId());
    }

}
