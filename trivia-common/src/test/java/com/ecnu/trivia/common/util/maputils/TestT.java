/** Created by Jack Chen at 12/24/2014 */
package com.ecnu.trivia.common.util.maputils;

import java.util.Date;

/** @author Jack Chen */
public class TestT {
    private long id;
    private String name;
    private String password;
    private Date date;

    public TestT() {
    }

    public TestT(long id, String name, String password, Date date) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
