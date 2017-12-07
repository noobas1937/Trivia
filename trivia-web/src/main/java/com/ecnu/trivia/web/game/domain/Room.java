package com.ecnu.trivia.web.game.domain;

import com.ecnu.trivia.common.component.domain.Column;
import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.Id;
import com.ecnu.trivia.common.component.domain.Key;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

public class Room extends Domain<Room>{

    @Id(generated = true)
    @Column(jdbcType = JdbcType.INTEGER)
    private Integer id;

    @Column(value = "room_name", jdbcType = JdbcType.VARCHAR)
    private String roomName;

    @Column(value = "status", jdbcType = JdbcType.INTEGER)
    private String status;

    @Column(value = "gmt_create", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtCreated;

    @Column(value = "gmt_modified", jdbcType = JdbcType.TIMESTAMP)
    private Timestamp gmtModified;

    public Room() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
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
