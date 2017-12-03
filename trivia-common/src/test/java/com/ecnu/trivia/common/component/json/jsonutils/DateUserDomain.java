/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.json.jsonutils;

import java.util.Date;

/** @author Jack Chen */
public class DateUserDomain extends UserDomain {
    private Date userDate;

    public DateUserDomain() {
    }

    public DateUserDomain(String username, String password, Date userDate) {
        super(username, password);
        this.userDate = userDate;
    }

    public Date getUserDate() {
        return userDate;
    }

    public void setUserDate(Date userDate) {
        this.userDate = userDate;
    }
}
