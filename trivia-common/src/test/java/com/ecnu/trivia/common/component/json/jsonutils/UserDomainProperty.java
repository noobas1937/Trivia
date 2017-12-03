/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json.jsonutils;

/** @author Jack Chen */
public class UserDomainProperty {
    private long id;

    public UserDomainProperty() {
    }

    public UserDomainProperty(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
