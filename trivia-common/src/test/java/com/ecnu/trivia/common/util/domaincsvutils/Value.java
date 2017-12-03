/** Created by Jack Chen at 12/5/2014 */
package com.ecnu.trivia.common.util.domaincsvutils;

import java.util.Date;

/** @author Jack Chen */
public class Value {
    private int id;
    private String username;
    private String password;
    private boolean valid;
    private Date date;

    public Value() {
    }

    public Value(int id, String username, String password, boolean valid, Date date) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.valid = valid;
        this.date = date;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        Value value = (Value) o;

        if(id != value.id)
            return false;
        if(valid != value.valid)
            return false;
        if(date != null ? !date.equals(value.date) : value.date != null)
            return false;
        if(password != null ? !password.equals(value.password) : value.password != null)
            return false;
        if(username != null ? !username.equals(value.username) : value.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (valid ? 1 : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
