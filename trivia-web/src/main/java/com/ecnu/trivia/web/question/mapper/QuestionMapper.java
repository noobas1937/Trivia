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
package com.ecnu.trivia.web.question.mapper;

import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.rbac.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionMapper extends Mapper<Question> {

    /**添加问题**/
    void addQuestion(@Param("content") String content,@Param("chooseA") String chooseA,@Param("chooseB") String chooseB,
                     @Param("chooseC") String chooseC,@Param("chooseD") String chooseD,@Param("answer") Integer answer,
                     @Param("type") Integer type);

    /**删除问题**/
    void deleteQuestion(@Param("id") Integer questionId);

    /**通过id获取问题**/
    Question getQuestionById(@Param("id") Integer questionId);

    /**修改问题**/
    void modifyQuestion(@Param("id") Integer id,@Param("content") String content,@Param("chooseA") String chooseA,@Param("chooseB") String chooseB,
                        @Param("chooseC") String chooseC,@Param("chooseD") String chooseD,@Param("answer") Integer answer,
                        @Param("type") Integer type);
}
