/** Created by Jack Chen at 12/7/2014 */
package com.ecnu.trivia.common.util.classutils;


import java.util.Date;

/**
 * 用于测试copyInstance(instanceA,class)
 *
 * @author Jack Chen
 */
public class InstanceB {
    private int id;
    private String username;
    private Date password;

    public InstanceB() {
    }

    public InstanceB(int id, String username, Date password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getPassword() {
        return password;
    }

    public void setPassword(Date password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        InstanceB instanceB = (InstanceB) o;

        if(id != instanceB.id)
            return false;
        if(password != null ? !password.equals(instanceB.password) : instanceB.password != null)
            return false;
        if(username != null ? !username.equals(instanceB.username) : instanceB.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
