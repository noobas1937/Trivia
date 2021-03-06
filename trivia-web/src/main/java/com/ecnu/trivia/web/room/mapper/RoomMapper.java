/**
 * Mapper 编写要求（做到 java 代码中不含sql语句）：
 *  1、Mapper由两部分组成：Interface + xml(sql)
 *  2、Interface置于java文件夹下，xml文件置于 resource相同目录下
 *  3、保证Interface名称与xml文件名称一样
 *  4、在xml文件顶部绑定Interface文件
 *  5、Interface内函数无需加 public 修饰（接口默认 public）
 *  6、请保证Interface方法名称与 Mapper 中对应 sql 的 id 一致
 *  7、参数请使用 @Param 标注变量名（与xml中参数名一致）
 *  8、请使用 @Repository 注解标明接口
 *
 * @author Jack Chen
 */
package com.ecnu.trivia.web.room.mapper;

import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.room.domain.Room;
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.rbac.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomMapper extends Mapper<Room> {
    /**获取房间列表*/
    List<RoomVO>  getRoomList();

    /**分页获取房间列表*/
    List<Room>  getRoomByPage(@Param("pno")Integer pno, @Param("pagesize")Integer PAGE_SIZE);


    /**根据房间ID获取房间信息*/
    RoomVO getRoomById(@Param("id")Integer id);

    /**根据房间ID删除房间*/
    void deleteRoomById(@Param("id")Integer id);

    /**根据房间名字查找房间*/
    Room getRoomByName(@Param("name")String name);

    /**增加房间*/
    void addRoomByName(@Param("name")String name);

    /**修改房间名字*/
    void modifyRoomName(@Param("id")Integer id,@Param("name")String name);

    /**根据player_id获取房间*/
    Room getRoomByPlayerID(@Param("id")Integer playerID);

    /**根据user_id获取房间*/
    Room getRoomByUserID(@Param("id")Integer userId);

    /**改变房间状态**/
    void updateRoomStatus(@Param("id")Integer id,@Param("status")Integer roomStatus);
}
