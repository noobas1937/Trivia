/** Created by Jack Chen at 12/9/2014 */
package com.ecnu.trivia.common.component.json.jsonutils;

import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.Key;

/** @author Jack Chen */
public class UserDomain extends Domain<UserDomain> {
    private String username;
    private String password;
    private UserDomainProperty property;

    public UserDomain() {
    }

    public UserDomain(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDomainProperty getProperty() {
        return property;
    }

    public void setProperty(UserDomainProperty property) {
        this.property = property;
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

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        UserDomain user = (UserDomain) o;

        if(password != null ? !password.equals(user.password) : user.password != null)
            return false;
        if(username != null ? !username.equals(user.username) : user.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public Key key() {
        return Key.of(username, password);
    }

    @Override
    public void clearKey() {
        username = null;
        password = null;
    }
}
