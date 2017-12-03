/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.json.jsonutils;

import java.util.Date;

/** @author Jack Chen */
public class DateUser extends User {
    private Date date;

    public DateUser() {
    }

    public DateUser(String username, String password, Date date) {
        super(username, password);
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
