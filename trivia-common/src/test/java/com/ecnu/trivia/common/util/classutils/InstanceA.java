/** Created by Jack Chen at 12/7/2014 */
package com.ecnu.trivia.common.util.classutils;


/**
 * 用于测试copyInstance(map,class)
 *
 * @author Jack Chen
 */
public class InstanceA {
    private long id;
    private String username;
    private String password;
    private boolean editFlag;

    public InstanceA() {
    }

    public InstanceA(long id, String username, String password, boolean editFlag) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.editFlag = editFlag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEditFlag() {
        return editFlag;
    }

    public void setEditFlag(boolean editFlag) {
        this.editFlag = editFlag;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        InstanceA instanceA = (InstanceA) o;

        if(editFlag != instanceA.editFlag)
            return false;
        if(id != instanceA.id)
            return false;
        if(password != null ? !password.equals(instanceA.password) : instanceA.password != null)
            return false;
        if(username != null ? !username.equals(instanceA.username) : instanceA.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (editFlag ? 1 : 0);
        return result;
    }
}
