package com.ecnu.trivia.web.question.domain;

import com.ecnu.trivia.common.component.domain.Column;
import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.Id;
import com.ecnu.trivia.common.component.domain.Key;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

/**
 * 问题实体类
 * @author Jak Chen
 */
public class Question extends Domain<Question> {

    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "description", jdbcType = JdbcType.VARCHAR)
    private String description;

    @Column(value = "type_id", jdbcType = JdbcType.INTEGER)
    private Integer typeId;

    @Column(value = "choose_a", jdbcType = JdbcType.VARCHAR)
    private String chooseA;

    @Column(value = "choose_b", jdbcType = JdbcType.VARCHAR)
    private String chooseB;

    @Column(value = "choose_c", jdbcType = JdbcType.VARCHAR)
    private String chooseC;

    @Column(value = "choose_d", jdbcType = JdbcType.VARCHAR)
    private String chooseD;

    @Column(value = "answer", jdbcType = JdbcType.INTEGER)
    private Integer answer;

    @Column(value = "status", jdbcType = JdbcType.INTEGER)
    private Integer status;

    @Column(value = "gmt_created", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    @Override
    public Key key() {
        return Key.of(id);
    }

    @Override
    public void clearKey() {
        id = 0;
    }
}
