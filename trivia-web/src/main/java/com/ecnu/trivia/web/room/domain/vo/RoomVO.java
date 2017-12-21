package com.ecnu.trivia.web.room.domain.vo;

import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.domain.vo.PlayerVO;

import java.sql.Timestamp;
import java.util.List;

public class RoomVO {
    private Integer id;
    private String roomName;
    private Integer status;
    private Timestamp gmtCreated;
    private Timestamp gmtModified;
    private Game game;
    private List<PlayerVO> playerList;

    public RoomVO() {
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

    public List<PlayerVO> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerVO> playerList) {
        this.playerList = playerList;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
