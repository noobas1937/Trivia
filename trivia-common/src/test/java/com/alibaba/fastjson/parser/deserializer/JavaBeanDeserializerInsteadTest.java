package com.alibaba.fastjson.parser.deserializer;

import com.ecnu.trivia.common.component.json.JsonUtils;
import com.ecnu.trivia.common.component.test.testng.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.List;

/**
 * @author liang.liang
 */
public class JavaBeanDeserializerInsteadTest extends BaseTest {


    @DataProvider
    public Object[][] p4testDeserialze() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //------------------------------------只有sex枚举为空-------------------------------------//start
        //1 sex为空，放在最后
        objects = new Object[paramLength];
        objects[0] = "{\"age\":20,\"faith\":\"buddhism\",\"name\":\"李福勇\",\"sex\":\"\"}";
        objects[1] = new User("李福勇", null, 20, Faith.buddhism);
        objectsList.add(objects);

        //2 sex为空，放在最前面
        objects = new Object[paramLength];
        objects[0] = "{\"sex\":\"\",\"age\":20,\"faith\":\"buddhism\",\"name\":\"李福勇\"}";
        objects[1] = new User("李福勇", null, 20, Faith.buddhism);
        objectsList.add(objects);

        //3 sex为空，放在中间
        objects = new Object[paramLength];
        objects[0] = "{\"age\":20,\"faith\":\"buddhism\",\"sex\":\"\",\"name\":\"李福勇\"}";
        objects[1] = new User("李福勇", null, 20, Faith.buddhism);
        objectsList.add(objects);
        //------------------------------------只有sex枚举为空-------------------------------------//end

        //------------------------------------所有枚举为空-------------------------------------//start
        //3 所有枚举都为空，放在最后
        objects = new Object[paramLength];
        objects[0] = "{\"age\":20,\"name\":\"李福勇\",\"faith\":\"\",\"sex\":\"\"}";
        objects[1] = new User("李福勇", null, 20, null);
        objectsList.add(objects);

        //3 所有枚举都为空，放在前面
        objects = new Object[paramLength];
        objects[0] = "{\"faith\":\"\",\"sex\":\"\",\"name\":\"李福勇\",\"age\":20}";
        objects[1] = new User("李福勇", null, 20, null);
        objectsList.add(objects);

        //3 所有枚举都为空，放在中间
        objects = new Object[paramLength];
        objects[0] = "{\"age\":20,\"faith\":\"\",\"sex\":\"\",\"name\":\"李福勇\"}";
        objects[1] = new User("李福勇", null, 20, null);
        objectsList.add(objects);
        //------------------------------------所有枚举为空-------------------------------------//end

        //所有枚举都不为空
        objects = new Object[paramLength];
        objects[0] = "{\"age\":20,\"faith\":\"chunge\",\"sex\":\"man\",\"name\":\"李福勇\"}";
        objects[1] = new User("李福勇", Sex.man, 20, Faith.chunge);
        objectsList.add(objects);

        //不包含枚举字段
        objects = new Object[paramLength];
        objects[0] = "{\"name\":\"李福勇\"}";
        objects[1] = new User().setName("李福勇");

        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testDeserialze")
    public void testDeserialze(String json, User expectValue) throws Exception {
        User value = JsonUtils.parse(json, User.class);
        Assert.assertEquals(value, expectValue);
    }

    public JavaBeanDeserializerInsteadTest() {
    }

    //---------------------------------------测试用内部类------------------------------------------//start
    enum Sex {
        man("男"),
        woman("女");

        private String desc;

        Sex(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

   enum Faith {
        god("上帝"),
        buddhism("佛祖"),
        chunge("春哥");

        private String desc;

        Faith(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }

    static class User {
        private String name;
        private Sex sex = Sex.man;
        private int age;
        private Faith faith;

        public User() {
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if(age != user.age) return false;
            if(!name.equals(user.name)) return false;
            if(sex != user.sex) return false;

            return faith == user.faith;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (sex != null ? sex.hashCode() : 0);
            result = 31 * result + age;
            result = 31 * result + (faith != null ? faith.hashCode() : 0);
            return result;
        }

        public User(String name, Sex sex, int age, Faith faith) {
            this.name = name;
            this.sex = sex;
            this.age = age;
            this.faith = faith;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Faith getFaith() {
            return faith;
        }

        public void setFaith(Faith faith) {
            this.faith = faith;
        }

        public String getName() {
            return name;
        }

        public User setName(String name) {
            this.name = name;
            return this;
        }

        public Sex getSex() {
            return sex;
        }

        public void setSex(Sex sex) {
            this.sex = sex;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", sex=" + sex +
                    '}';
        }
    }
    //---------------------------------------测试用内部类------------------------------------------//end
}
