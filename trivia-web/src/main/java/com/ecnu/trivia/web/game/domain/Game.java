package com.ecnu.trivia.web.game.domain;

import com.ecnu.trivia.common.component.domain.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

/**
 * 玩家实体类
 * @author Jack Chen
 */
@Table("game")
public class Game extends Domain<Game>{
    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "current_player_id", jdbcType = JdbcType.INTEGER)
    private Integer currentPlayerId;

    @Column(value = "dice_number", jdbcType = JdbcType.INTEGER)
    private Integer diceNumber;

    @Column(value = "room_id", jdbcType = JdbcType.INTEGER)
    private Integer roomId;

    @Column(value = "question_id", jdbcType = JdbcType.INTEGER)
    private Integer questionId;

    @Column(value = "stage", jdbcType = JdbcType.INTEGER)
    private String stage;

    @Column(value = "gmt_created", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    public Game() {
    }

    public Game(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(Integer currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public Integer getDiceNumber() {
        return diceNumber;
    }

    public void setDiceNumber(Integer diceNumber) {
        this.diceNumber = diceNumber;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
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
        return Key.of(id);
    }

    @Override
    public void clearKey() {
        id = 0;
    }
}
