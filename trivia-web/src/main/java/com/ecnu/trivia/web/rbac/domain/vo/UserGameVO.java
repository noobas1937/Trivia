package com.ecnu.trivia.web.rbac.domain.vo;

public class UserGameVO {
    private String nickName;
    private String roomName;
    Integer status;
    Integer balance;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer banlance) {
        this.balance = banlance;
    }
}
