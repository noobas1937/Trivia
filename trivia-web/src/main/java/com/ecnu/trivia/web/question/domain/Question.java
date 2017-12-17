package com.ecnu.trivia.web.question.domain;

import com.ecnu.trivia.common.component.domain.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

/**
 * 问题实体类
 * @author Jak Chen
 */
@Table("question")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getChooseA() {
        return chooseA;
    }

    public void setChooseA(String chooseA) {
        this.chooseA = chooseA;
    }

    public String getChooseB() {
        return chooseB;
    }

    public void setChooseB(String chooseB) {
        this.chooseB = chooseB;
    }

    public String getChooseC() {
        return chooseC;
    }

    public void setChooseC(String chooseC) {
        this.chooseC = chooseC;
    }

    public String getChooseD() {
        return chooseD;
    }

    public void setChooseD(String chooseD) {
        this.chooseD = chooseD;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
