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
import com.ecnu.trivia.web.room.domain.vo.RoomVO;
import com.ecnu.trivia.web.rbac.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomMapper extends Mapper<User> {
    List<RoomVO>  getRoomList();
    RoomVO getRoomById(@Param("id")Integer id);
}
