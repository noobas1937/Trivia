package com.ecnu.trivia.web.question.domain;

import com.ecnu.trivia.common.component.domain.Column;
import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.Id;
import com.ecnu.trivia.common.component.domain.Key;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

/**
 * 问题类型实体类
 * @author Jack Chen
 */
public class QuestionType extends Domain<QuestionType> {

    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "name", jdbcType = JdbcType.VARCHAR)
    private String name;

    @Column(value = "gmt_created", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    public QuestionType() {
    }

    public QuestionType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Timestamp gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public Key key() {
        return null;
    }

    @Override
    public void clearKey() {

    }
}
