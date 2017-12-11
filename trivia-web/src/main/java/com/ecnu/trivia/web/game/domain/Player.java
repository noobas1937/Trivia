package com.ecnu.trivia.web.game.domain;

import com.ecnu.trivia.common.component.domain.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

/**
 * 玩家实体类
 * @author Jack Chen
 */
@Table("player")
public class Player extends Domain<Player>{
    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "user_id", jdbcType = JdbcType.INTEGER)
    private Integer userId;

    @Column(value = "room_id", jdbcType = JdbcType.INTEGER)
    private Integer roomId;

    @Column(value = "balance", jdbcType = JdbcType.INTEGER)
    private Integer balance;

    @Column(value = "position", jdbcType = JdbcType.INTEGER)
    private Integer position;

    @Column(value = "status", jdbcType = JdbcType.INTEGER)
    private String status;

    @Column(value = "gmt_created", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    public Player() {
    }

    public Player(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    @Override
    public Key key() {
        return Key.of(id);
    }

    @Override
    public void clearKey() {
        id = 0;
    }
}
