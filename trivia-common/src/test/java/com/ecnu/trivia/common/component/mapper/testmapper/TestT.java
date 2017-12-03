/** Created by Jack Chen at 11/30/2014 */
package com.ecnu.trivia.common.component.mapper.testmapper;


import com.ecnu.trivia.common.component.cache.utils.CacheKey;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import com.ecnu.trivia.common.component.domain.*;
import org.apache.ibatis.type.JdbcType;

/** @author Jack Chen */
@Table("test_t")
@CacheKey(prefix = "testT", cacheName = CacheValue.THREAD)
public class TestT extends Domain<TestT> {
    public static final String TABLE_DDL =
            "CREATE TABLE `test_t` (`id` bigint(20) NOT NULL AUTO_INCREMENT,  `name` varchar(255) DEFAULT NULL,  PRIMARY KEY (`id`)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

    public static final String SQL_FIND_BY_ID = "select * from test_t where id = ?";

    @Id(generated = true)
    @Column(jdbcType = JdbcType.BIGINT)
    private long id;

    @Column(jdbcType = JdbcType.VARCHAR)
    private String name;

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

    @Override
    public Key key() {
        return Key.of(id);
    }

    @Override
    public void clearKey() {
        id = 0;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        TestT testT = (TestT) o;

        if(id != testT.id)
            return false;
        if(name != null ? !name.equals(testT.name) : testT.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestT{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
